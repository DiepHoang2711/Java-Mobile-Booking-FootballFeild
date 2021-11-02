package com.example.football_field_booking.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.football_field_booking.BuildConfig;
import com.example.football_field_booking.FootballFieldDetailActivity;
import com.example.football_field_booking.MainActivity;
import com.example.football_field_booking.R;
import com.example.football_field_booking.adapters.CartAdapter;
import com.example.football_field_booking.daos.FootballFieldDAO;
import com.example.football_field_booking.daos.UserDAO;
import com.example.football_field_booking.dtos.BookingDTO;
import com.example.football_field_booking.dtos.CartItemDTO;
import com.example.football_field_booking.dtos.TimePickerDTO;
import com.example.football_field_booking.dtos.UserDocument;
import com.example.football_field_booking.utils.APISERVICE;
import com.example.football_field_booking.utils.Client;
import com.example.football_field_booking.utils.Data;
import com.example.football_field_booking.utils.Sender;
import com.example.football_field_booking.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.paypal.checkout.PayPalCheckout;
import com.paypal.checkout.approve.Approval;
import com.paypal.checkout.approve.OnApprove;
import com.paypal.checkout.config.CheckoutConfig;
import com.paypal.checkout.config.Environment;
import com.paypal.checkout.config.PaymentButtonIntent;
import com.paypal.checkout.createorder.CreateOrder;
import com.paypal.checkout.createorder.CreateOrderActions;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.OrderIntent;
import com.paypal.checkout.createorder.ProcessingInstruction;
import com.paypal.checkout.createorder.UserAction;
import com.paypal.checkout.order.Amount;
import com.paypal.checkout.order.AppContext;
import com.paypal.checkout.order.CaptureOrderResult;
import com.paypal.checkout.order.OnCaptureComplete;
import com.paypal.checkout.order.Order;
import com.paypal.checkout.order.PurchaseUnit;
import com.paypal.checkout.paymentbutton.PayPalButton;
import com.squareup.okhttp.ResponseBody;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment {

    private ListView lvCart;
    private TextView txtTotal, txtCartEmpty;
    private Float total = 0f;
    private CartAdapter cartAdapter;
    private static final SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
    private static final SimpleDateFormat dfBooking = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

    PayPalButton payPalButton;

    private APISERVICE apiservice;

    private ProgressDialog prdCheckout;

    private Utils util;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        lvCart = view.findViewById(R.id.lvCart);
        txtTotal = view.findViewById(R.id.txtTotal);
        txtCartEmpty = view.findViewById(R.id.txtCartEmpty);
        payPalButton = view.findViewById(R.id.payPalButton);

        util = new Utils();
        cartAdapter = new CartAdapter(getActivity());

        loadData();

        cartAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                total = 0f;
                List<CartItemDTO> cart = cartAdapter.getCart();
                for (CartItemDTO dto : cart) {
                    total += dto.getTotal();
                }
                txtTotal.setText("$" + total);
                checkEmptyCart();
            }
        });

        Bundle bundle = getArguments();
        if (bundle != null) {
            String action = bundle.getString("check_out_success");
            if (action != null && action.equals("check_out_success")) {
                try {
                    booking();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        prdCheckout = new ProgressDialog(getContext());
        CheckoutConfig config = new CheckoutConfig(
                getActivity().getApplication(),
                getString(R.string.YOUR_CLIENT_ID),
                Environment.SANDBOX,
                String.format("%s://paypalpay", BuildConfig.APPLICATION_ID),
                CurrencyCode.USD,
                UserAction.PAY_NOW,
                PaymentButtonIntent.CAPTURE
        );
        PayPalCheckout.setConfig(config);

        payPalButton.setup(
                new CreateOrder() {
                    @Override
                    public void create(@NotNull CreateOrderActions createOrderActions) {
                        ArrayList purchaseUnits = new ArrayList<>();
                        purchaseUnits.add(
                                new PurchaseUnit.Builder()
                                        .amount(
                                                new Amount.Builder()
                                                        .currencyCode(CurrencyCode.USD)
                                                        .value(total + "")
                                                        .build()
                                        )
                                        .build()
                        );
                        Order order = new Order(
                                OrderIntent.CAPTURE,
                                new AppContext.Builder()
                                        .userAction(UserAction.PAY_NOW)
                                        .build(),
                                purchaseUnits,
                                ProcessingInstruction.NO_INSTRUCTION
                        );
                        createOrderActions.create(order, (CreateOrderActions.OnOrderCreated) null);

                    }
                },
                new OnApprove() {
                    @Override
                    public void onApprove(@NotNull Approval approval) {
                        util.showProgressDialog(prdCheckout, "Checkout", "Please wait for checkout");
                        FootballFieldDAO fieldDAO = new FootballFieldDAO();
                        List<CartItemDTO> cart = cartAdapter.getCart();
                        try {
                            if (isValidBookingDate(cart)) {
                                fieldDAO.getBookingByFieldAndDate(cart).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            boolean flag = true;
                                            outerLoop:
                                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                                CartItemDTO itemInBooking = doc.toObject(CartItemDTO.class);
                                                List<TimePickerDTO> timePickerInBooking = itemInBooking.getTimePicker();
                                                for (TimePickerDTO timePickerDTO : timePickerInBooking) {
                                                    for (CartItemDTO itemInCart : cart) {
                                                        if ((itemInCart.getFieldInfo().getFieldID().equals(itemInBooking.getFieldInfo().getFieldID())
                                                                && (itemInCart.getDate().equals(itemInBooking.getDate())))) {
                                                            if (itemInCart.getTimePicker().contains(timePickerDTO)) {
                                                                flag = false;
                                                                showError(itemInCart.getFieldInfo().getName() + " in " + timePickerDTO.getStart()
                                                                        + "-" + timePickerDTO.getEnd() + " already booking by someone");
                                                                break outerLoop;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            if (flag) {
                                                approval.getOrderActions().capture(new OnCaptureComplete() {
                                                    @Override
                                                    public void onCaptureComplete(@NotNull CaptureOrderResult result) {
                                                        Toast.makeText(getContext(), "Check out success", Toast.LENGTH_LONG).show();
                                                        try {
                                                            booking();
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                            }else{
                                                Toast.makeText(getContext(), "Check out fail", Toast.LENGTH_LONG).show();
                                                prdCheckout.cancel();
                                            }
                                        } else {
                                            task.getException().printStackTrace();
                                            Toast.makeText(getActivity(), "Get data booking fail", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        apiservice = Client.getRetrofit("https://fcm.googleapis.com/").create(APISERVICE.class);
        return view;
    }

    private void loadData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UserDAO userDAO = new UserDAO();
            userDAO.getCart(user.getUid()).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        List<CartItemDTO> cart = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            CartItemDTO cartItemDTO = doc.toObject(CartItemDTO.class);
                            cart.add(cartItemDTO);
                            total += cartItemDTO.getTotal();
                        }
                        txtTotal.setText("$" + total);
                        cartAdapter.setCart(cart);
                        lvCart.setAdapter(cartAdapter);

                        lvCart.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                CartItemDTO dto = (CartItemDTO) lvCart.getItemAtPosition(i);
                                Intent intent = new Intent(getActivity(), FootballFieldDetailActivity.class);
                                intent.putExtra("fieldID", dto.getFieldInfo().getFieldID());
                                startActivity(intent);
                            }
                        });

                        checkEmptyCart();
                    } else {
                        Toast.makeText(getActivity(), "Load cart fail", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }

    private void checkEmptyCart() {
        if (cartAdapter.getCart().isEmpty()) {
            payPalButton.setVisibility(View.GONE);
            txtCartEmpty.setVisibility(View.VISIBLE);
        } else {
            payPalButton.setVisibility(View.VISIBLE);
            txtCartEmpty.setVisibility(View.GONE);
        }
    }

    private void booking() throws Exception {
        FootballFieldDAO fieldDAO = new FootballFieldDAO();
        List<CartItemDTO> cart = cartAdapter.getCart();
        UserDAO userDAO = new UserDAO();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Calendar calendar = Calendar.getInstance();
        String dateBooking = dfBooking.format(calendar.getTime());
        if (user != null) {
            BookingDTO bookingDTO = new BookingDTO(user.getUid(), dateBooking, total);
            userDAO.booking(bookingDTO, cart).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        for (CartItemDTO cartItemDTO : cart) {
                            fieldDAO.getFieldByID(cartItemDTO.getFieldInfo().getFieldID())
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            String userID = documentSnapshot.getString("ownerInfo.userID");
                                            userDAO.getUserById(userID).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    List<String> tokens = task.getResult().toObject(UserDocument.class).getTokens();
                                                    if (tokens != null) {
                                                        for (String token : tokens) {
                                                            String title = "You have a new booking";
                                                            String body = cartItemDTO.getFieldInfo().getName() + " is booked by" + user.getDisplayName();
                                                            Data data = new Data(body, title);
                                                            sendNotification(token, data);
                                                        }
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    e.printStackTrace();
                                                }
                                            });
                                        }
                                    });
                        }
                        Toast.makeText(getActivity(), "Booking Success", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.putExtra("action", "view history");
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), "Booking Fail", Toast.LENGTH_SHORT).show();
                    }
                    prdCheckout.cancel();
                }

            });

        }
    }

    private void sendNotification(String token, Data data) {
        Sender sender = new Sender(data, token);
        apiservice.sendNotification(sender)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code() == 200) {
                            if (!response.isSuccessful()) {
                                Toast.makeText(getContext(), "Send notification faild", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                    }
                });
    }


    private boolean isValidBookingDate(List<CartItemDTO> cart) throws Exception {
        boolean result = true;
        Calendar calendar = Calendar.getInstance();
        String now = df.format(calendar.getTime());
        for (CartItemDTO cartItemDTO : cart) {
            String date = cartItemDTO.getDate();
            if (date.compareTo(now) < 1) {
                showError("Field name: " + cartItemDTO.getFieldInfo().getName() + " date book invalid");
                result = false;
                break;
            }
        }
        return result;
    }

    private void showError(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
    }
}