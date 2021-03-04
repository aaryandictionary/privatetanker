package com.project.privatetanker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.project.privatetanker.Database.Database;
import com.project.privatetanker.Database.UserInterface;
import com.project.privatetanker.Models.Login.LoginData;
import com.project.privatetanker.Models.Login.LoginModel;
import com.project.privatetanker.Models.Login.LoginResponse;
import com.project.privatetanker.Utils.DataProcessor;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpVerification extends AppCompatActivity {

    private DataProcessor dataProcessor;
    private String mVerificationId;
    private FirebaseAuth mAuth;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    CountDownTimer countDownTimer;

    private EditText textOtp;
    private RelativeLayout relVerifyOtp;
    private ContentLoadingProgressBar progress;
    private TextView txtChangeMobile,txtTerms,txtInstruction,txtResendOtp;

    private boolean isResendEnabled=false;

    private ProgressDialog progressDialog;

    private String token=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        initializeView();

        dataProcessor=DataProcessor.getInstance(this);
        mAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait...");

        String mobile=dataProcessor.getString(DataProcessor.PHONE);
        txtInstruction.setText("One time password is sent to your mobile \n+91 "+mobile);

        getToken();
        showProgress();
        sendVerificationCode(mobile);
        txtChangeMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataProcessor.deleteAll();
                onBackPressed();
            }
        });

        relVerifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (token==null){
                    showSnackBar("Token not available. Try again");
                    getToken();
                }else {
                    if (!TextUtils.isEmpty(textOtp.getText().toString())&&textOtp.getText().toString().length()==6)
                        verifyVerificationCode(textOtp.getText().toString());
                    else
                        showSnackBar("Enter the correct OTP");
                }
            }
        });

        txtResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isResendEnabled){
                    resendVerificationCode(mobile);
                }
            }
        });

    }

    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobile,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
    }

    private void resendVerificationCode(String mobile){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+ mobile,
                60  ,
                TimeUnit.SECONDS,
                this,
                mCallbacks,
                mResendToken);
    }

    private void startTimer(){
        countDownTimer= new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished/1000!=0){
                    isResendEnabled=false;
                    txtResendOtp.setText("Resend OTP in " + millisUntilFinished / 1000+" seconds");
                }else{
                    countDownTimer.onFinish();
                }
            }

            public void onFinish() {
                countDownTimer.cancel();
                isResendEnabled=true;
                txtResendOtp.setText("Resend OTP");
                txtResendOtp.setTextColor(getResources().getColor(R.color.colorSkyBlue));
                isResendEnabled=true;
            }

        }.start();
    }


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                textOtp.setText(code);
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            hideProgress();
            showSnackBar("Failed to send OTP"+e.getMessage());
            if (countDownTimer!=null){
                countDownTimer.cancel();
            }
            isResendEnabled=true;
            txtResendOtp.setText("Resend OTP");
            txtChangeMobile.setTextColor(getResources().getColor(R.color.colorSkyBlue));
            txtResendOtp.setVisibility(View.VISIBLE);
            isResendEnabled=true;
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            hideProgress();
            showSnackBar("OTP sent successfully");
            mVerificationId = s;
            startTimer();
            txtResendOtp.setVisibility(View.VISIBLE);
            txtResendOtp.setTextColor(getResources().getColor(R.color.colorGrayDark));
            isResendEnabled=false;
            mResendToken = forceResendingToken;
        }
    };

    private void verifyVerificationCode(String otp) {
        showProgress();
        showSnackBar("Verifying One Time Password");

        textOtp.setEnabled(false);
//        txtChangeMobile.setEnabled(false);
        if (countDownTimer!=null){
            countDownTimer.cancel();
        }
//        txtResendOtp.setText(getResources().getString(R.string.login_verifying));
//        txtForgotPassword.setTextColor(getResources().getColor(R.color.colorGrey));
//        txtForgotPassword.setVisibility(View.VISIBLE);
        isResendEnabled=false;
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            checkUser();
                        }else {
                            hideProgress();
                            try {
                                throw task.getException();
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                showSnackBar("Invalid Verification code");
                                textOtp.requestFocus();
                            }  catch(Exception e) {
                                showSnackBar("Something went wrong. Try again");
                                Toast.makeText(OtpVerification.this,e.getMessage(),Toast.LENGTH_LONG).show();
                            }

                            if (countDownTimer!=null){
                                countDownTimer.cancel();
                            }
                            txtResendOtp.setText("Resend OTP");
                            txtResendOtp.setTextColor(getResources().getColor(R.color.colorSkyBlue));
                            isResendEnabled=true;
                            textOtp.setEnabled(true);
                        }

                    }
                });
    }

    private void checkUser(){
        progressDialog.show();
        LoginModel loginModel=new LoginModel();
        loginModel.setPhone(dataProcessor.getString(DataProcessor.PHONE));
        loginModel.setPassword(mAuth.getCurrentUser().getUid());
        loginModel.setToken(token);

        UserInterface userInterface= Database.getClient(this).create(UserInterface.class);
        userInterface.loginUser(loginModel).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()){
                    if (response.body() != null && response.body().getStatus().equals("SUCCESS")) {
                        saveUserData(response.body().getData());
                    }
                }else {
                    showSnackBar("Something went wrong. Try again");
                }
                progressDialog.dismiss();
                hideProgress();
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                hideProgress();
                progressDialog.dismiss();
                showSnackBar("Something went wrong. Try again");
            }
        });
    }

    private void saveUserData(LoginData loginData){
        dataProcessor.saveString(DataProcessor.CUSTOMER_TYPE,loginData.getCustomerType());
        dataProcessor.saveString(DataProcessor.FIREBASE_UID,mAuth.getCurrentUser().getUid());
        dataProcessor.saveString(DataProcessor.IMAGE,loginData.getImage());
        dataProcessor.saveString(DataProcessor.NAME,loginData.getName());
        dataProcessor.saveInteger(DataProcessor.STATUS,loginData.getStatus());
        dataProcessor.saveString(DataProcessor.TOKEN,loginData.getToken());
        dataProcessor.saveInteger(DataProcessor.USER_ID,loginData.getId());

        if (loginData.getName()==null||TextUtils.isEmpty(loginData.getName())){
            dataProcessor.saveBoolean(DataProcessor.REQUIRED_PROFILE_COMPLETION,true);
        }
        progressDialog.dismiss();

        Intent intent=new Intent(OtpVerification.this,LockScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void showProgress(){
        progress.show();
        relVerifyOtp.setVisibility(View.GONE);
    }

    private void hideProgress(){
        progress.hide();
        relVerifyOtp.setVisibility(View.VISIBLE);
    }

    private void showSnackBar(String message){
        Snackbar.make(findViewById(android.R.id.content),message,Snackbar.LENGTH_SHORT).show();
    }

    private void initializeView(){
        textOtp=findViewById(R.id.textOtp);
        relVerifyOtp=findViewById(R.id.relVerifyOtp);
        progress=findViewById(R.id.progress);
        txtChangeMobile=findViewById(R.id.txtChangeMobile);
        txtTerms=findViewById(R.id.txtTerms);
        txtInstruction=findViewById(R.id.txtInstruction);
        txtResendOtp=findViewById(R.id.txtResendOtp);
    }

    private void getToken(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (task.isSuccessful()){
                    token=task.getResult().getToken();
                }
            }
        });
    }
}