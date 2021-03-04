package com.project.privatetanker.Utils;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

public class App extends Application {

    public static final String CHANNEL_NOTIFICATION ="channel_notification";
    public static App sInstance;

    public static App get() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        sInstance=this;
        super.onCreate();

        NotificationChannel channelAutobackup = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channelAutobackup = new NotificationChannel(
                    CHANNEL_NOTIFICATION,
                    "Notification",
                    NotificationManager.IMPORTANCE_HIGH);
            channelAutobackup.setDescription("");
            NotificationManager notificationManagerAB = getSystemService(NotificationManager.class);
            notificationManagerAB.createNotificationChannel(channelAutobackup);
        }
    }
}
