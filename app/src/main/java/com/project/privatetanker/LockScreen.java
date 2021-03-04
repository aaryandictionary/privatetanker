package com.project.privatetanker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.project.privatetanker.Utils.DataProcessor;

public class LockScreen extends AppCompatActivity implements View.OnClickListener{

    private DataProcessor dataProcessor;

    private TextView txtSkip,txtTitle,txtInstruction,txtCode1,txtCode2,txtCode3,txtCode4,txtReset;
    private ImageButton imgBtnOne,imgBtnTwo,imgBtnThree,imgBtnFour,imgBtnFive,imgBtnSix,imgBtnSeven,imgBtnEight,imgBtnNine,imgBtnBackspace,imgBtnZero,imgBtnOk;

    private int count=0;
    private String password="";

    private boolean isNew=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);
        initializeView();

        dataProcessor=DataProcessor.getInstance(this);

        if (dataProcessor.getBoolean(DataProcessor.IS_PASSCODE_SET)){
            setOldPasswordConfig();
            isNew=false;
        }else{
            setNewPasswordConfig();
            isNew=true;
        }

        txtReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showResetDialog();
            }
        });

        txtSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataProcessor.saveBoolean(DataProcessor.IS_PASSCODE_SKIP,true);
                Intent intent=new Intent(LockScreen.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void showResetDialog(){
        AlertDialog.Builder alert=new AlertDialog.Builder(this);
        alert.setMessage("You will be logged out to reset your secure PIN.\n Sure want to logout?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                dataProcessor.deleteAll();
                Intent intent=new Intent(LockScreen.this,Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    private void setNewPasswordConfig(){
        txtTitle.setText("Set secure PIN");
        txtInstruction.setText("You will require this PIN every time you open the app");
        txtSkip.setVisibility(View.VISIBLE);
        txtReset.setVisibility(View.GONE);
    }

    private void setOldPasswordConfig(){
        txtTitle.setText("Verify your PIN");
        txtInstruction.setText("Enter your secure PIN to access the app");
        txtSkip.setVisibility(View.GONE);
        txtReset.setVisibility(View.VISIBLE);
    }

    private void initializeView(){
        txtSkip=findViewById(R.id.txtSkip);
        txtTitle=findViewById(R.id.txtTitle);
        txtInstruction=findViewById(R.id.txtInstruction);
        txtCode1=findViewById(R.id.txtCode1);
        txtCode2=findViewById(R.id.txtCode2);
        txtCode3=findViewById(R.id.txtCode3);
        txtCode4=findViewById(R.id.txtCode4);
        txtReset=findViewById(R.id.txtReset);

        imgBtnOne=findViewById(R.id.imgBtnOne);
        imgBtnTwo=findViewById(R.id.imgBtnTwo);
        imgBtnThree=findViewById(R.id.imgBtnThree);
        imgBtnFour=findViewById(R.id.imgBtnFour);
        imgBtnFive=findViewById(R.id.imgBtnFive);
        imgBtnSix=findViewById(R.id.imgBtnSix);
        imgBtnSeven=findViewById(R.id.imgBtnSeven);
        imgBtnEight=findViewById(R.id.imgBtnEight);
        imgBtnNine=findViewById(R.id.imgBtnNine);
        imgBtnBackspace=findViewById(R.id.imgBtnBackspace);
        imgBtnZero=findViewById(R.id.imgBtnZero);
        imgBtnOk=findViewById(R.id.imgBtnOk);

        imgBtnOne.setOnClickListener(this);
        imgBtnTwo.setOnClickListener(this);
        imgBtnThree.setOnClickListener(this);
        imgBtnFour.setOnClickListener(this);
        imgBtnFive.setOnClickListener(this);
        imgBtnSix.setOnClickListener(this);
        imgBtnSeven.setOnClickListener(this);
        imgBtnEight.setOnClickListener(this);
        imgBtnNine.setOnClickListener(this);
        imgBtnBackspace.setOnClickListener(this);
        imgBtnZero.setOnClickListener(this);
        imgBtnOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.imgBtnOne:
                setPasscode("1");
                break;
            case R.id.imgBtnTwo:
                setPasscode("2");
                break;
            case R.id.imgBtnThree:
                setPasscode("3");
                break;
            case R.id.imgBtnFour:
                setPasscode("4");
                break;
            case R.id.imgBtnFive:
                setPasscode("5");
                break;
            case R.id.imgBtnSix:
                setPasscode("6");
                break;
            case R.id.imgBtnSeven:
                setPasscode("7");
                break;
            case R.id.imgBtnEight:
                setPasscode("8");
                break;
            case R.id.imgBtnNine:
                setPasscode("9");
                break;
            case R.id.imgBtnZero:
                setPasscode("0");
                break;
            case R.id.imgBtnBackspace:
                backSpace();
                break;
            case R.id.imgBtnOk:
                verifyPasscode();
                break;
        }
    }

    private void verifyPasscode(){
        if (password.length()==4){
            if (isNew){
                dataProcessor.saveBoolean(DataProcessor.IS_PASSCODE_SET,true);
                dataProcessor.saveBoolean(DataProcessor.IS_PASSCODE_SKIP,false);
                dataProcessor.saveString(DataProcessor.PASSCODE,password);
            }
            if (TextUtils.equals(password,dataProcessor.getString(DataProcessor.PASSCODE))){
                Intent intent=new Intent(LockScreen.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }else{
                showSnackBar("Incorrect password");
            }
        }else{
            showSnackBar("Please check the password");
        }

    }

    private void showSnackBar(String message){
        Snackbar.make(findViewById(android.R.id.content),message,Snackbar.LENGTH_SHORT).show();
    }

    private void backSpace(){
        if (!password.isEmpty()){
            if (password.length()==1){
                password="";
                count=0;
                txtCode1.setText("");
            }else{
                if (password.length()==2){
                    password= String.valueOf(password.charAt(0));
                }else{
                    password=password.substring(0,password.length()-1);
                }
                count--;
                if (count==3){
                    txtCode4.setText("");
                }else if (count==2){
                    txtCode3.setText("");
                }else if (count==1){
                    txtCode2.setText("");
                }
            }

        }

    }

    private void setPasscode(String code){
        if (count<4){
            password=password.concat(code);
            count++;
            if (count==1){
                txtCode1.setText(code);
            }else if (count==2){
                txtCode2.setText(code);
            }else if (count==3){
                txtCode3.setText(code);
            }else if (count==4){
                txtCode4.setText(code);
            }
        }
    }
}