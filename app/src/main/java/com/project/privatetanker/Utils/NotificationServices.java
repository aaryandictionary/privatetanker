package com.project.privatetanker.Utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.project.privatetanker.Database.Database;
import com.project.privatetanker.Database.UserInterface;
import com.project.privatetanker.MainActivity;
import com.project.privatetanker.Models.UpdateTokenModel;
import com.project.privatetanker.Models.User.UserResponse;
import com.project.privatetanker.R;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.project.privatetanker.Utils.App.CHANNEL_NOTIFICATION;

public class NotificationServices extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        UpdateTokenModel updateTokenModel=new UpdateTokenModel();
        updateTokenModel.setFcm_token(s);
        updateTokenModel.setId(DataProcessor.getInstance(getApplicationContext()).getInteger(DataProcessor.USER_ID));

        updateToken(updateTokenModel);

        DataProcessor.getInstance(getApplicationContext()).saveString(DataProcessor.FCM_TOKEN,s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
//        Toast.makeText(getApplicationContext(),remoteMessage.getNotification().getBody(),Toast.LENGTH_LONG).show();
//        Log.d("notification",remoteMessage.getNotification().getImageUrl().toString());
       /* try {
            Glide.with(getApplicationContext()).asBitmap().load(remoteMessage.getNotification().getImageUrl()).submit().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }*/
        Map<String,String> payload=new HashMap<>();
            payload.put("title",remoteMessage.getNotification().getTitle());
            payload.put("body",remoteMessage.getNotification().getBody());

            try{
                if (!TextUtils.isEmpty(Objects.requireNonNull(remoteMessage.getNotification().getImageUrl()).toString())){
                    payload.put("image",remoteMessage.getNotification().getImageUrl().toString());
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            showNotification(payload);
    }
    private void showNotification(Map<String, String> payload) {
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,CHANNEL_NOTIFICATION);
        builder.setSmallIcon(R.drawable.ic_launcher_background);
        builder.setContentTitle(payload.get("title"));
        builder.setContentText(payload.get("body"));
        builder.setAutoCancel(true);
        builder.setPriority(Notification.PRIORITY_MAX);
        if (!TextUtils.isEmpty(payload.get("image"))){
            try {
                Bitmap bitmap= Glide.with(this).asBitmap().load(payload.get("image")).submit().get();
                builder.setLargeIcon(bitmap);
                builder.setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap))
                        .setLargeIcon(null);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
//        builder.setAutoCancel(true);

//        builder.setColor(getResources().getColor(R.color.colorPrimaryDark));

        Intent resultIntent=new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder= TaskStackBuilder.create(this);

        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent=stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0,builder.build());
    }

    private void updateToken(UpdateTokenModel updateTokenModel){
        UserInterface userInterface= Database.getClient(this).create(UserInterface.class);
        userInterface.updateToken(updateTokenModel).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {

            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {

            }
        });
    }
}
