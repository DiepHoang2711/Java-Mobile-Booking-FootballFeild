package com.example.football_field_booking.utils;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class MyApplication extends Application {
    public static final String CHANEL_ID="push_notification_id";

    @Override
    public void onCreate() {
        super.onCreate();
        createChanelNotificatin();
    }

    private void createChanelNotificatin() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel(CHANEL_ID,"PushNotification",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
            NotificationManager manager =getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
