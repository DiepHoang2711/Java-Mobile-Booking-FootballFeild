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
    public static final String  GROUP_KEY="group_key";
    private int i=1;

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
            intent = new Intent(MyFirebaseMessagingService.this, OwnerMainActivity.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(MyFirebaseMessagingService.this, 0
                    , intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(MyFirebaseMessagingService.this, MyApplication.CHANEL_ID)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.mipmap.ic_logo_round)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            Notification notification = builder.getNotification();
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.notify(i++, notification);
                onDeletedMessages();
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
