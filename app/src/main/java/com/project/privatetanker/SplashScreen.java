package com.project.privatetanker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.project.privatetanker.Database.Database;
import com.project.privatetanker.Database.UserInterface;
import com.project.privatetanker.Models.UpdateTokenModel;
import com.project.privatetanker.Models.User.UserResponse;
import com.project.privatetanker.Utils.DataProcessor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreen extends AppCompatActivity {

    FirebaseAuth mAuth;

    DataProcessor dataProcessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mAuth = FirebaseAuth.getInstance();
        dataProcessor = DataProcessor.getInstance(this);

        if (mAuth.getCurrentUser()!=null){
            UpdateTokenModel updateTokenModel=new UpdateTokenModel();
            updateTokenModel.setId(dataProcessor.getInteger(DataProcessor.USER_ID));
            updateTokenModel.setFcm_token(dataProcessor.getString(DataProcessor.FCM_TOKEN));
            updateToken(updateTokenModel);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mAuth.getCurrentUser() == null) {
                    dataProcessor.deleteAll();
                    Intent intent = new Intent(SplashScreen.this, Login.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    if (dataProcessor.getBoolean(DataProcessor.IS_PASSCODE_SKIP)) {
                        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    } else {
                        if (dataProcessor.getBoolean(DataProcessor.IS_PASSCODE_SET)) {
                            Intent intent = new Intent(SplashScreen.this, LockScreen.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }

                }
            }
        }, 1500);
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