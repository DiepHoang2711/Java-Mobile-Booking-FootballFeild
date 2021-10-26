package com.example.football_field_booking.daos;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.football_field_booking.dtos.BookingDTO;
import com.example.football_field_booking.dtos.CartItemDTO;
import com.example.football_field_booking.dtos.FootballFieldDTO;
import com.example.football_field_booking.dtos.RatingDTO;
import com.example.football_field_booking.dtos.TimePickerDTO;
import com.example.football_field_booking.dtos.UserDTO;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.firestore.core.OrderBy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firestore.v1.StructuredQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDAO {

    private static final String COLLECTION_FOOTBALL_FIELD = "footballFields";
    public static final String CONST_OF_PROJECT = "constOfProject";
    public static final String USER_IMAGES_FOLDER = "user_images";
    public static final String SUB_COLLECTION_CART = "cart";
    public static final String SUB_COLLECTION_BOOKING_INFO = "bookingInfo";
    public static final String SUB_COLLECTION_BOOKING_DETAIL = "bookingDetail";
    public static final String SUB_COLLECTION_BOOKING = "booking";
    public static final String SUB_COLLECTION_RATING = "rating";
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private static final String COLLECTION_USERS = "users";
    public static final String SUB_COLLECTION_TOKENS = "tokens";

    public UserDAO() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    public Task<Void> createUser(UserDTO userDTO) {
        DocumentReference doc = db.collection(COLLECTION_USERS).document(userDTO.getUserID());
        Map<String, Object> data = new HashMap<>();
        data.put("userInfo", userDTO);
        return doc.set(data);
    }


    public Task<DocumentSnapshot> getUserById(String id) {
        DocumentReference doc = db.collection(COLLECTION_USERS).document(id);
        return doc.get();
    }

    public Task<QuerySnapshot> getAllUser() {
        return db.collection(COLLECTION_USERS).get();
    }

    public Task<Void> updateUser(UserDTO userDTO, List<FootballFieldDTO> list) {
        WriteBatch batch = db.batch();

        DocumentReference docUser = db.collection(COLLECTION_USERS).document(userDTO.getUserID());
        Map<String, Object> dataUser = new HashMap<>();
        dataUser.put("userInfo.email", userDTO.getEmail());
        dataUser.put("userInfo.fullName", userDTO.getFullName());
        dataUser.put("userInfo.phone", userDTO.getPhone());
        dataUser.put("userInfo.role", userDTO.getRole());
        dataUser.put("userInfo.status", userDTO.getStatus());
        dataUser.put("userInfo.photoUri", userDTO.getPhotoUri());

        batch.update(docUser, dataUser);

        if (userDTO.getRole().equals("owner") && list != null) {

            for (FootballFieldDTO dto : list) {
                DocumentReference doc = db.collection(COLLECTION_FOOTBALL_FIELD).document(dto.getFieldID());
                Map<String, Object> dataInFBfield = new HashMap<>();
                dataInFBfield.put("ownerInfo.email", userDTO.getEmail());
                dataInFBfield.put("ownerInfo.fullName", userDTO.getFullName());
                dataInFBfield.put("ownerInfo.phone", userDTO.getPhone());
                dataInFBfield.put("ownerInfo.role", userDTO.getRole());
                dataInFBfield.put("ownerInfo.status", userDTO.getStatus());
                dataInFBfield.put("ownerInfo.photoUri", userDTO.getPhotoUri());

                batch.update(doc, dataInFBfield);
            }
        }
        return batch.commit();

    }

    public Task<Void> updatePassword(String password) {
        FirebaseUser user = mAuth.getCurrentUser();
        return user.updatePassword(password);
    }

    public Task<Void> deleteUser(String userID) {

        DocumentReference doc = db.collection(COLLECTION_USERS).document(userID);
        Map<String, Object> data = new HashMap<>();
        data.put("userInfo.status", "deleted");
        return doc.update(data);
    }

    public Task<DocumentSnapshot> getConstOfUser() {
        DocumentReference doc = db.collection(CONST_OF_PROJECT).document("const");
        return doc.get();
    }

    public Task<Uri> uploadImgUserToFirebase(Uri uri) throws Exception {

        StorageReference mStoreRef = FirebaseStorage.getInstance().getReference(USER_IMAGES_FOLDER)
                .child(System.currentTimeMillis() + ".png");
        UploadTask uploadTask = mStoreRef.putFile(uri);
        return uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                return mStoreRef.getDownloadUrl();
            }
        });
    }

    public Task<Void> addToCart(CartItemDTO cartItemDTO, String cartItemID, String userID) {
        if (cartItemID == null) {
            DocumentReference doc = db.collection(COLLECTION_USERS).document(userID).collection(SUB_COLLECTION_CART).document();
            cartItemDTO.setCartItemID(doc.getId());
            cartItemDTO.setFieldAndDate(cartItemDTO.getFieldInfo().getFieldID() + cartItemDTO.getDate());
            return doc.set(cartItemDTO);
        } else {
            WriteBatch batch = db.batch();
            DocumentReference doc = db.collection(COLLECTION_USERS).document(userID).collection(SUB_COLLECTION_CART).document(cartItemID);

            for (TimePickerDTO dto : cartItemDTO.getTimePicker()) {
                Map<String, Object> data = new HashMap<>();
                data.put("timePicker", FieldValue.arrayUnion(dto));
                batch.update(doc, data);
            }
            Map<String, Object> data = new HashMap<>();
            data.put("total", FieldValue.increment(cartItemDTO.getTotal()));
            batch.update(doc, data);
            return batch.commit();
        }
    }

    public Task<QuerySnapshot> getCart(String userID) {
        return db.collection(COLLECTION_USERS).document(userID).collection(SUB_COLLECTION_CART).get();
    }

    public Task<QuerySnapshot> getItemInCartByFieldAndDate(String userID, String fieldID, String date) {
        return db.collection(COLLECTION_USERS).document(userID).collection(SUB_COLLECTION_CART)
                .whereEqualTo("fieldAndDate", fieldID + date).get();

    }

    public Task<Void> deleteCartItem(String userID, String cartItemID) {
        DocumentReference doc = db.collection(COLLECTION_USERS).document(userID).collection(SUB_COLLECTION_CART).document(cartItemID);
        return doc.delete();
    }

    public Task<Void> booking(BookingDTO bookingDTO, List<CartItemDTO> cart) {
        List<DocumentReference> listDocInUser = new ArrayList<>();
        List<DocumentReference> listDocInField = new ArrayList<>();
        List<DocumentReference> listDocCart = new ArrayList<>();
        DocumentReference docBooking = db.collection(COLLECTION_USERS).document(bookingDTO.getUserID())
                .collection(SUB_COLLECTION_BOOKING_INFO).document();
        bookingDTO.setBookingID(docBooking.getId());
        for (CartItemDTO cartItemDTO : cart) {
            DocumentReference docUser = docBooking.collection(SUB_COLLECTION_BOOKING_DETAIL).document(cartItemDTO.getCartItemID());
            DocumentReference docField = db.collection(COLLECTION_FOOTBALL_FIELD).document(cartItemDTO.getFieldInfo().getFieldID())
                    .collection(SUB_COLLECTION_BOOKING).document(cartItemDTO.getCartItemID());
            DocumentReference docCart = db.collection(COLLECTION_USERS).document(bookingDTO.getUserID())
                    .collection(SUB_COLLECTION_CART).document(cartItemDTO.getCartItemID());
            listDocInUser.add(docUser);
            listDocInField.add(docField);
            listDocCart.add(docCart);
        }

        return db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                transaction.set(docBooking, bookingDTO);

                for (int i = 0; i < cart.size(); i++) {
                    DocumentReference docUser = listDocInUser.get(i);
                    DocumentReference docField = listDocInField.get(i);
                    DocumentReference docCart = listDocCart.get(i);
                    CartItemDTO bookingItem = cart.get(i);
                    Map<String, Object> data = new HashMap<>();
                    data.put("alreadyRating", false);
                    transaction.set(docUser, bookingItem);
                    transaction.set(docField, bookingItem);
                    transaction.set(docUser, data, SetOptions.merge());
                    transaction.delete(docCart);
                }

                return null;
            }
        });
    }

    public Task<Void> saveToken(String token, String userID) throws Exception {
        DocumentReference reference = db.collection(COLLECTION_USERS).document(userID);
        return reference.update("tokens",FieldValue.arrayUnion(token));
    }

    public Task<Void> deleteToken(String token,String userID) throws Exception{
        return db.collection(COLLECTION_USERS).document(userID).update("tokens",FieldValue.arrayRemove(token));
    }

    public Task<QuerySnapshot> getAllBooking (String userID) {
        return db.collection(COLLECTION_USERS).document(userID)
                .collection(SUB_COLLECTION_BOOKING_INFO).orderBy("bookingDate", Query.Direction.ASCENDING).get();
    }

    public Task<Void> rating (RatingDTO ratingDTO, String bookingID, String bookingDetailID) {
        DocumentReference docUser = db.collection(COLLECTION_USERS).document(ratingDTO.getUserInfo().getUserID())
                .collection(SUB_COLLECTION_RATING).document();
        DocumentReference docField = db.collection(COLLECTION_FOOTBALL_FIELD).document(ratingDTO.getFieldInfo().getFieldID())
                .collection(SUB_COLLECTION_RATING).document();
        DocumentReference docBooking = db.collection(COLLECTION_USERS).document(ratingDTO.getUserInfo().getUserID())
                .collection(SUB_COLLECTION_BOOKING_INFO).document(bookingID)
                .collection(SUB_COLLECTION_BOOKING_DETAIL).document(bookingDetailID);
        return db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                transaction.set(docUser, ratingDTO);
                transaction.set(docField, ratingDTO);
                Map<String, Object> data = new HashMap<>();
                data.put("alreadyRating", true);
                transaction.update(docBooking, data);
                return null;
            }
        });

    }

    public Task<QuerySnapshot> getAllBookingDetail (String userID, String bookingID) {
        return db.collection(COLLECTION_USERS).document(userID)
                .collection(SUB_COLLECTION_BOOKING_INFO).document(bookingID)
                .collection(SUB_COLLECTION_BOOKING_DETAIL).orderBy("date", Query.Direction.ASCENDING).get();
    }

}
