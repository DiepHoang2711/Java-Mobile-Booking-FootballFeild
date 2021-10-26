package com.example.football_field_booking.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.football_field_booking.LoginActivity;
import com.example.football_field_booking.MainActivity;
import com.example.football_field_booking.OwnerMainActivity;
import com.example.football_field_booking.R;
import com.example.football_field_booking.daos.FootballFieldDAO;
import com.example.football_field_booking.daos.UserDAO;
import com.example.football_field_booking.dtos.UserDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private Intent intent;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> stringMap = remoteMessage.getData();
        String title = stringMap.get("title");
        String body = stringMap.get("body");
        sendNotification(title, body);
    }


    private void sendNotification(String title, String message) {

        try {

//            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//            intent=new Intent(MyFirebaseMessagingService.this, LoginActivity.class);
//            if(user!=null){
//                UserDAO userDAO=new UserDAO();
//                userDAO.getUserById(user.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        String role= documentSnapshot.getString("userInfo.role");
//                        Log.e("UserDTO ROLE:",role);
//                        if(role.equals("owner") && user.getUid().equals(receiverID)){
//                            intent=new Intent(MyFirebaseMessagingService.this, OwnerMainActivity.class);
//                        }else{
//                            FirebaseAuth.getInstance().signOut();
////                            intent=new Intent(MyFirebaseMessagingService.this, LoginActivity.class);
////                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                        }
//                    }
//                });
//            }
            intent = new Intent(MyFirebaseMessagingService.this, OwnerMainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(MyFirebaseMessagingService.this, 0
                    , intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(MyFirebaseMessagingService.this, MyApplication.CHANEL_ID)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.logo)
                    .setContentIntent(pendingIntent);
            Notification notification = builder.getNotification();
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.notify(1, notification);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }
}
