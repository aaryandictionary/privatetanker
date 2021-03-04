package com.project.privatetanker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.project.privatetanker.Utils.DataProcessor;

public class Login extends AppCompatActivity {

    private EditText textMobile;
    private RelativeLayout relSendOtp;
    private ContentLoadingProgressBar progress;
    private TextView txtTerms;

    private DataProcessor dataProcessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeView();

        dataProcessor = DataProcessor.getInstance(this);

        relSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(textMobile.getText()) && textMobile.getText().length() == 10) {
                    progress.show();
                    relSendOtp.setVisibility(View.GONE);
                    dataProcessor.saveString(DataProcessor.PHONE,textMobile.getText().toString());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            relSendOtp.setVisibility(View.VISIBLE);
                            progress.hide();
                            Intent intent = new Intent(Login.this, OtpVerification.class);
                            startActivity(intent);
                        }
                    }, 1000);
                }
            }

        });
    }

    private void initializeView() {
        textMobile = findViewById(R.id.textMobile);
        relSendOtp = findViewById(R.id.relSendOtp);
        progress = findViewById(R.id.progress);
        txtTerms = findViewById(R.id.txtTerms);
    }
}