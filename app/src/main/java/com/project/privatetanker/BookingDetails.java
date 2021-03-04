package com.project.privatetanker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.project.privatetanker.Adapters.BookingParticularAdapter;
import com.project.privatetanker.Database.BookingInterface;
import com.project.privatetanker.Database.Database;
import com.project.privatetanker.Database.UserInterface;
import com.project.privatetanker.Models.BookingParticular.BookingParticularData;
import com.project.privatetanker.Models.Bookings.BookingData;
import com.project.privatetanker.Models.Bookings.BookingResponse;
import com.project.privatetanker.Models.User.UserData;
import com.project.privatetanker.Models.User.UserResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingDetails extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 100;
    private ImageView imgStatus;
    private TextView txtStatus, txtTime, txtOrderId, txtCustomerName, txtCustomerType, txtPhone, txtAddressType, txtCapacity, txtDate, txtTimeDelivery, txtAddress, txtInstruction;
    private CircleImageView circularProfile;
    private RecyclerView recyclerBill;
    private TextView txtAmount, txtMessage;
    private RelativeLayout relCancelOrder, relModifyBooking, relContactSupport, relReorder;

    private Integer id = -1;
    private BookingInterface bookingInterface;

    private BookingParticularAdapter bookingParticularAdapter;

    private ProgressDialog progressDialog;
    private RelativeLayout relBookingMain,relFooter;

    private NestedScrollView nestedScroll;

    private int clickType=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);
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

        id = getIntent().getIntExtra("id", -1);
        bookingInterface = Database.getClient(this).create(BookingInterface.class);
        bookingParticularAdapter = new BookingParticularAdapter(this);
        recyclerBill.setAdapter(bookingParticularAdapter);

        getBookingDetails();

        relContactSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        relCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelOrder();
            }
        });

        relModifyBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickType=0;
                checkLocationPermission();
            }
        });

        relReorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickType=1;
                checkLocationPermission();
            }
        });

        nestedScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                /*if (scrollY > oldScrollY) {
                    hideViews();
                }
                if (scrollY < oldScrollY) {
                    showViews();
                }*/
            }
        });

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
        ActivityCompat.requestPermissions(BookingDetails.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_LOCATION_PERMISSION);
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            isLocationGranted = true;
            Intent intent = new Intent(BookingDetails.this, BookTanker.class);
            if (clickType==0){
                intent.putExtra("id", id);
            }else if (clickType==1){
                intent.putExtra("bookingId", id);
            }
            startActivity(intent);
            finish();
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


    /*private void hideViews(){
        relFooter.animate()
                .translationY(relFooter.getHeight())
                .alpha(1.0f)
                .setListener(null);
    }

    private void showViews(){
        relFooter.animate()
                .translationY(0)
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
//                        relFooter.setVisibility(View.GONE);
                    }
                });
    }*/

    private void cancelOrder() {
        progressDialog.show();
        bookingInterface.cancelBooking(id).enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus().equals("SUCCESS")) {
                        setStatus("CANCELLED");
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    private void getBookingDetails() {
        progressDialog.show();
        bookingInterface.getBookingDetails(id).enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                if (response.isSuccessful()) {
                    BookingData bookingData = null;
                    if (response.body() != null && response.body().getStatus().equals("SUCCESS")) {
                        if (response.body() != null) {
                            bookingData = response.body().getData();
                            txtAddress.setText(bookingData.getAddress());
                            txtAddressType.setText(bookingData.getAddress());
                            txtAmount.setText("Rs " + bookingData.getAmount());
                            txtCapacity.setText(bookingData.getCapacity());
                            String date = changeToDD(bookingData.getDate());
                            if (date != null)
                                txtDate.setText(date);
                            else txtDate.setText(bookingData.getDate());

                            txtTime.setText(bookingData.getUpdatedAt());
                            txtInstruction.setText(bookingData.getNotes());
                            txtMessage.setText(bookingData.getMessage());
                            txtOrderId.setText("ORDER ID : " + bookingData.getOrderId());
                            txtStatus.setText(bookingData.getStatusMessage());
                            txtTimeDelivery.setText(bookingData.getTime());
                            setUserData(bookingData.getUser());
                            setStatus(bookingData.getStatus());
                            setParticulars(bookingData.getParticulars());
                            relBookingMain.setVisibility(View.VISIBLE);
                        }
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    private String changeToDD(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat sdfS = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            return sdf.format(sdfS.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setParticulars(List<BookingParticularData> bookingParticularDataList) {
        bookingParticularAdapter.setBookingParticularList(bookingParticularDataList);
    }

    private void setUserData(UserData userData) {
        try {
            txtCustomerType.setText(userData.getCustomerType());
            txtCustomerName.setText(userData.getName());
            txtPhone.setText("+91 "+userData.getPhone());
            Glide.with(BookingDetails.this).load(userData.getImage()).placeholder(R.drawable.ic_profile_placeholder).into(circularProfile);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setStatus(String status) {
        try {
            if (TextUtils.equals(status, "PROCESSING")) {
                Glide.with(this).load(R.drawable.processing).into(imgStatus);
                relCancelOrder.setVisibility(View.VISIBLE);
                relModifyBooking.setVisibility(View.VISIBLE);
                relCancelOrder.setVisibility(View.GONE);
                relReorder.setVisibility(View.GONE);
            } else if (TextUtils.equals(status, "CANCELLED")) {
                Glide.with(this).load(R.drawable.failed).into(imgStatus);
                relContactSupport.setVisibility(View.VISIBLE);
                relReorder.setVisibility(View.GONE);
                relCancelOrder.setVisibility(View.GONE);
                relModifyBooking.setVisibility(View.GONE);
            } else if (TextUtils.equals(status, "COMPLETED")) {
                Glide.with(this).load(R.drawable.success).into(imgStatus);
                relReorder.setVisibility(View.VISIBLE);
                relModifyBooking.setVisibility(View.GONE);
                relCancelOrder.setVisibility(View.GONE);
                relContactSupport.setVisibility(View.GONE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initializeView() {
        relFooter=findViewById(R.id.relFooter);
        nestedScroll=findViewById(R.id.nestedScroll);
        relBookingMain = findViewById(R.id.relBookingMain);
        imgStatus = findViewById(R.id.imgStatus);
        txtStatus = findViewById(R.id.txtStatus);
        txtTime = findViewById(R.id.txtTime);
        txtOrderId = findViewById(R.id.txtOrderId);
        txtCustomerName = findViewById(R.id.txtCustomerName);
        txtCustomerType = findViewById(R.id.txtCustomerType);
        txtPhone = findViewById(R.id.txtPhone);
        txtAddressType = findViewById(R.id.txtAddressType);
        txtCapacity = findViewById(R.id.txtCapacity);
        txtDate = findViewById(R.id.txtDate);
        txtTimeDelivery = findViewById(R.id.txtTimeDelivery);
        txtAddress = findViewById(R.id.txtAddress);
        txtInstruction = findViewById(R.id.txtInstruction);
        circularProfile = findViewById(R.id.circularProfile);

        recyclerBill = findViewById(R.id.recyclerBill);
        recyclerBill.setHasFixedSize(true);
        recyclerBill.setLayoutManager(new LinearLayoutManager(this));

        txtAmount = findViewById(R.id.txtAmount);
        txtMessage = findViewById(R.id.txtMessage);
        relCancelOrder = findViewById(R.id.relCancelOrder);
        relModifyBooking = findViewById(R.id.relModifyBooking);
        relContactSupport = findViewById(R.id.relContactSupport);
        relReorder = findViewById(R.id.relReorder);
    }
}