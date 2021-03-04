package com.project.privatetanker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.ContentLoadingProgressBar;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.project.privatetanker.Database.Database;
import com.project.privatetanker.Database.UserInterface;
import com.project.privatetanker.Models.Address.AddressData;
import com.project.privatetanker.Models.Address.AddressResponse;
import com.project.privatetanker.Models.Login.LoginData;
import com.project.privatetanker.Models.User.UserData;
import com.project.privatetanker.Models.User.UserResponse;
import com.project.privatetanker.Utils.DataProcessor;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfile extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 23;
    private int editType = 1;
    //0- Complete Profile
    //1- Edi Profile

    private CircleImageView circularProfile;
    private EditText textName, textCustomerType, textMobile;
    private TextView txtChange, txtLocation, txtChangeLocation;
    private LinearLayout linLocation;
    private RelativeLayout relSave;

    private UserData userData = null;
    private UserInterface userInterface;
    private DataProcessor dataProcessor;

    private File selectedFile = null;

    private RelativeLayout relMain;
    private ContentLoadingProgressBar progress;

    private ProgressDialog progressDialog;
    private boolean isLocationGranted = false;

    private boolean isProfileComplete = false;
    private boolean isFirstLocationSaved = false;

    private AddressData addressData=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        initializeView();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait...");

        userInterface = Database.getClient(this).create(UserInterface.class);
        dataProcessor = DataProcessor.getInstance(this);

        textMobile.setText(dataProcessor.getString(DataProcessor.PHONE));
        textCustomerType.setText(dataProcessor.getString(DataProcessor.CUSTOMER_TYPE));
        try {
            Glide.with(this).load(dataProcessor.getString(DataProcessor.IMAGE)).placeholder(R.drawable.ic_profile_placeholder).into(circularProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (getIntent().getIntExtra("type", 1) == 0) {
            getSupportActionBar().setTitle("Complete Profile");
            setCompleteProfileConfig();
            isProfileComplete = true;
            dataProcessor.saveBoolean(DataProcessor.FIRST_LOCATION_SAVED, false);
        } else {
            getSupportActionBar().setTitle("Edit Profile");
            setEditProfileConfig();
        }

        linLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLocationPermission();
            }
        });

        circularProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setRequestedSize(180, 180, CropImageView.RequestSizeOptions.RESIZE_INSIDE).start(EditProfile.this);
            }
        });

        relSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields())
                    updateProfile();
            }
        });
    }

    private boolean validateFields() {
        if (isProfileComplete && !isFirstLocationSaved) {
            showSnackBar("Select a location");
            return false;
        }
        if (TextUtils.isEmpty(textName.getText())) {
            showSnackBar("Enter your name");
            return false;
        }
        if (TextUtils.isEmpty(textCustomerType.getText())) {
            showSnackBar("Enter your designation");
            return false;
        }

        return true;
    }

    private void showSnackBar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0) {
                checkLocationPermission();
            }
        }
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(EditProfile.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_LOCATION_PERMISSION);
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            isLocationGranted = true;
            if (addressData==null){
                Intent intent = new Intent(EditProfile.this, MapActivity.class);
                startActivity(intent);
            }else {
                Intent intent = new Intent(EditProfile.this, MapActivity.class);
                intent.putExtra("id",addressData.getId());
                intent.putExtra("address",addressData.getAddress());
                intent.putExtra("address_type",addressData.getAddressType());
                intent.putExtra("lat",addressData.getLat());
                intent.putExtra("lng",addressData.getLng());
                startActivity(intent);
            }

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                showPermissionDialog();
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                showPermissionDialog();
            } else {
                requestLocationPermission();
            }
        } else {
            requestLocationPermission();
        }
    }

    private void showPermissionDialog() {
        androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(this).create();
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_permission_required, null);
        alertDialog.setView(view);

        RelativeLayout relRequestPermission = view.findViewById(R.id.relRequestPermission);
        relRequestPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                requestLocationPermission();
            }
        });

        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void setCompleteProfileConfig() {
        linLocation.setVisibility(View.VISIBLE);
        txtLocation.setText("Select your location");
    }

    private void setEditProfileConfig() {
        linLocation.setVisibility(View.GONE);
        getUserData();
    }

    private void getUserData() {
        relMain.setVisibility(View.GONE);
        progress.show();
        userInterface.user(dataProcessor.getInteger(DataProcessor.USER_ID)).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        try {
                            userData = response.body().getData();
                            saveUserData(userData);
                            textName.setText(userData.getName());
                            textCustomerType.setText(userData.getCustomerType());
                            textMobile.setText("+91 " + userData.getPhone());
                            Glide.with(EditProfile.this).load(userData.getImage()).placeholder(R.drawable.ic_profile_placeholder).into(circularProfile);
                            progress.hide();
                            relMain.setVisibility(View.VISIBLE);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                } else {
                    progress.hide();
                    Toast.makeText(EditProfile.this, "Something went wrong. Try again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                progress.hide();
                Toast.makeText(EditProfile.this, "Something went wrong. Try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfile() {
        progressDialog.show();
        RequestBody Id = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(dataProcessor.getInteger(DataProcessor.USER_ID)));
        RequestBody Name = RequestBody.create(MediaType.parse("text/plain"), textName.getText().toString());
        RequestBody CustomerType = RequestBody.create(MediaType.parse("text/plain"), textCustomerType.getText().toString());
        Call<UserResponse> call = null;

        if (selectedFile != null) {
            MultipartBody.Part imgTitle = MultipartBody.Part.createFormData("image",
                    selectedFile.getName(), RequestBody.create(MediaType.parse("image/*"), selectedFile));
            call = userInterface.updateProfile(Id, Name, CustomerType, imgTitle);
        } else {
            call = userInterface.updateProfile(Id, Name, CustomerType);
        }
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus().equals("SUCCESS")) {
                        Toast.makeText(EditProfile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        saveUserData(response.body().getData());
                        dataProcessor.saveBoolean(DataProcessor.REQUIRED_PROFILE_COMPLETION, false);
                    }
                } else {
                    Toast.makeText(EditProfile.this, "Something went wrong. Try again", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(EditProfile.this, "Something went wrong. Try again", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                if (result.getUri() != null) {
                    if (result.getUri().getPath() != null) {
                        selectedFile = new File(result.getUri().getPath());
                        circularProfile.setImageURI(result.getUri());
                    }
                }
            } else {
                Toast.makeText(this, "File selection failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dataProcessor.getInteger(DataProcessor.SELECTED_ADDRESS_ID) != -1) {
            getUserAddress(dataProcessor.getInteger(DataProcessor.SELECTED_ADDRESS_ID));
        }
        isFirstLocationSaved = dataProcessor.getBoolean(DataProcessor.FIRST_LOCATION_SAVED);
    }

    private void getUserAddress(Integer addressId) {
        userInterface.getAddressById(addressId).enqueue(new Callback<AddressResponse>() {
            @Override
            public void onResponse(Call<AddressResponse> call, Response<AddressResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus().equals("SUCCESS")) {
                        txtLocation.setText(response.body().getData().getAddress());
                        addressData=response.body().getData();
                    }
                }
            }

            @Override
            public void onFailure(Call<AddressResponse> call, Throwable t) {

            }
        });
    }

    private void saveUserData(UserData userData) {
        dataProcessor.saveString(DataProcessor.CUSTOMER_TYPE, userData.getCustomerType());
        dataProcessor.saveString(DataProcessor.IMAGE, userData.getImage());
        dataProcessor.saveString(DataProcessor.NAME, userData.getName());
        dataProcessor.saveInteger(DataProcessor.STATUS, userData.getStatus());
    }


    private void initializeView() {
        relMain = findViewById(R.id.relMain);
        progress = findViewById(R.id.progress);

        circularProfile = findViewById(R.id.circularProfile);
        textName = findViewById(R.id.textName);
        textCustomerType = findViewById(R.id.textCustomerType);
        textMobile = findViewById(R.id.textMobile);
        txtChange = findViewById(R.id.txtChange);
        txtLocation = findViewById(R.id.txtLocation);
        txtChangeLocation = findViewById(R.id.txtChangeLocation);
        linLocation = findViewById(R.id.linLocation);
        relSave = findViewById(R.id.relSave);
    }
}