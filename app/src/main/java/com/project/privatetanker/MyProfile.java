package com.project.privatetanker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.project.privatetanker.Adapters.UserAddressAdapter;
import com.project.privatetanker.Database.Database;
import com.project.privatetanker.Database.UserInterface;
import com.project.privatetanker.Models.Address.AddressData;
import com.project.privatetanker.Models.Address.AddressListResponse;
import com.project.privatetanker.Models.Address.AddressResponse;
import com.project.privatetanker.Models.User.UserData;
import com.project.privatetanker.Models.User.UserResponse;
import com.project.privatetanker.Utils.DataProcessor;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyProfile extends AppCompatActivity implements UserAddressAdapter.UserAddressClickEvent, Toolbar.OnMenuItemClickListener {

    private static final int EDIT_ADDRESS_REQUEST_CODE = 32;
    private static final int ADD_ADDRESS_REQUEST_CODE = 54;
    private static final int REQUEST_LOCATION_PERMISSION = 56;
    private CircleImageView circularProfile;
    private TextView txtName,txtCustomerType,txtMobile,txtAddAddress;
    private RecyclerView recyclerAddresses;

    private UserInterface userInterface;
    private DataProcessor dataProcessor;

    private UserData userData;
    private UserAddressAdapter userAddressAdapter;

    private ContentLoadingProgressBar progress;
    private RelativeLayout relMain;

    private boolean isLocationGranted=false;

    private int locationClickType=-1;
    private AddressData addressData=null;
    private TextView txtNoAddress;

    private int id=-1,position=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        initializeView();

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        dataProcessor=DataProcessor.getInstance(this);
        userInterface= Database.getClient(this).create(UserInterface.class);
        userAddressAdapter=new UserAddressAdapter(this,this);
        recyclerAddresses.setAdapter(userAddressAdapter);

        txtAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationClickType=0;
                checkLocationPermission();
            }
        });
        setData();
        getUserData();
        getUserAddress();
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
        ActivityCompat.requestPermissions(MyProfile.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_LOCATION_PERMISSION);
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            isLocationGranted = true;
            if (locationClickType==0){
                Intent intent=new Intent(MyProfile.this,MapActivity.class);
                startActivity(intent);
            }else if (locationClickType==1){
                if (addressData!=null){
                    Intent intent=new Intent(MyProfile.this,MapActivity.class);
                    intent.putExtra("id",id);
                    intent.putExtra("position",position);
                    intent.putExtra("address",addressData.getAddress());
                    intent.putExtra("address_type",addressData.getAddressType());
                    intent.putExtra("lat",addressData.getLat());
                    intent.putExtra("lng",addressData.getLng());
                    startActivityForResult(intent,EDIT_ADDRESS_REQUEST_CODE);
                }
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


    private void setData(){
        try {
            txtName.setText(dataProcessor.getString(DataProcessor.NAME));
            txtMobile.setText("+91 "+dataProcessor.getString(DataProcessor.PHONE));
            txtCustomerType.setText(dataProcessor.getString(DataProcessor.CUSTOMER_TYPE));
            Glide.with(this).load(dataProcessor.getString(DataProcessor.IMAGE)).placeholder(R.drawable.ic_profile_placeholder).into(circularProfile);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getUserAddress(){
        userInterface.addresses(dataProcessor.getInteger(DataProcessor.USER_ID)).enqueue(new Callback<AddressListResponse>() {
            @Override
            public void onResponse(Call<AddressListResponse> call, Response<AddressListResponse> response) {
                if (response.isSuccessful()){
                    if (response.body() != null && response.body().getStatus().equals("SUCCESS")) {
                        if (response.body().getData().size()!=0){
                            userAddressAdapter.setAddressList(response.body().getData());
                            txtNoAddress.setVisibility(View.GONE);
                        }
                        else txtNoAddress.setVisibility(View.VISIBLE);
                    }
                    progress.hide();
                }
            }

            @Override
            public void onFailure(Call<AddressListResponse> call, Throwable t) {

            }
        });
    }

    private void getUserData(){
        userInterface.user(dataProcessor.getInteger(DataProcessor.USER_ID)).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful()){
                    if (response.body() != null && response.body().getStatus().equals("SUCCESS")) {
                        try {
                            userData = response.body().getData();
                            saveUserData(userData);
                            Glide.with(MyProfile.this).load(userData.getImage()).into(circularProfile);
                            if (userData.getName() == null || TextUtils.isEmpty(userData.getName())) {
                                Intent intent = new Intent(MyProfile.this, EditProfile.class);
                                intent.putExtra("type", 0);
                                startActivity(intent);
                            } else {
                                txtName.setText(userData.getName());
                                txtCustomerType.setText(userData.getCustomerType());
                                txtMobile.setText("+91 " + userData.getPhone());
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {

            }
        });
    }

    private void saveUserData(UserData userData){
        dataProcessor.saveString(DataProcessor.CUSTOMER_TYPE,userData.getCustomerType());
        dataProcessor.saveString(DataProcessor.IMAGE,userData.getImage());
        dataProcessor.saveString(DataProcessor.NAME,userData.getName());
        dataProcessor.saveInteger(DataProcessor.STATUS,userData.getStatus());
        setData();
    }

    private void initializeView(){
        txtNoAddress=findViewById(R.id.txtNoAddress);
        relMain=findViewById(R.id.relMain);
        progress=findViewById(R.id.progress);
        circularProfile=findViewById(R.id.circularProfile);
        txtName=findViewById(R.id.txtName);
        txtCustomerType=findViewById(R.id.txtCustomerType);
        txtMobile=findViewById(R.id.txtMobile);
        txtAddAddress=findViewById(R.id.txtAddAddress);
        recyclerAddresses=findViewById(R.id.recyclerAddresses);
        recyclerAddresses.setLayoutManager(new LinearLayoutManager(this));
        recyclerAddresses.setHasFixedSize(true);
    }

    @Override
    public void onUserAddressClick(AddressData addressData,Integer id,int position) {
        this.id=id;
        locationClickType=1;
        this.position=position;
        this.addressData=addressData;
        checkLocationPermission();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId()==R.id.editProfile){
            Intent intent=new Intent(MyProfile.this,EditProfile.class);
            intent.putExtra("type",1);
            startActivityForResult(intent,ADD_ADDRESS_REQUEST_CODE);
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==ADD_ADDRESS_REQUEST_CODE){
            if (resultCode==RESULT_OK){
                assert data != null;
                Bundle bundle=data.getBundleExtra("add_address");
                AddressData addressData=new AddressData();
                assert bundle != null;
                addressData.setAddress(bundle.getString("address"));
                addressData.setAddressType(bundle.getString("address_type"));
                addressData.setId(bundle.getInt("id"));
                addressData.setUserId(bundle.getInt("user_id"));
                addressData.setCreatedAt(bundle.getString("created_at"));
                addressData.setUpdatedAt(bundle.getString("updated_at"));
                addressData.setLat(bundle.getDouble("lat"));
                addressData.setLng(bundle.getDouble("lng"));

                userAddressAdapter.addAddress(addressData);
            }
        }else if (requestCode==EDIT_ADDRESS_REQUEST_CODE){
            if (resultCode==RESULT_OK){
                assert data != null;
                Bundle bundle=data.getBundleExtra("edit_address");
                AddressData addressData=new AddressData();
                assert bundle != null;
                addressData.setAddress(bundle.getString("address"));
                addressData.setAddressType(bundle.getString("address_type"));
                addressData.setId(bundle.getInt("id"));
                addressData.setUserId(bundle.getInt("user_id"));
                addressData.setCreatedAt(bundle.getString("created_at"));
                addressData.setUpdatedAt(bundle.getString("updated_at"));
                addressData.setLat(bundle.getDouble("lat"));
                addressData.setLng(bundle.getDouble("lng"));
                int position=bundle.getInt("position");

                userAddressAdapter.updateAddress(addressData,position);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my_profile,menu);
        return super.onCreateOptionsMenu(menu);
    }
}