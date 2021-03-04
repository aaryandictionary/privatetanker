package com.project.privatetanker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.project.privatetanker.Adapters.BookingChargesAdapter;
import com.project.privatetanker.Database.BookingInterface;
import com.project.privatetanker.Database.Database;
import com.project.privatetanker.Database.UserInterface;
import com.project.privatetanker.Models.BookingParticular.BookingParticularModel;
import com.project.privatetanker.Models.Bookings.BookingModel;
import com.project.privatetanker.Models.Bookings.BookingResponse;
import com.project.privatetanker.Models.Charges.ChargeData;
import com.project.privatetanker.Models.Charges.ChargeListResponse;
import com.project.privatetanker.Models.User.UserData;
import com.project.privatetanker.Models.User.UserResponse;
import com.project.privatetanker.Utils.DataProcessor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmBooking extends AppCompatActivity {

    private BookingModel bookingModel;
    private boolean isEdit = false;

    private CircleImageView circularProfile;
    private TextView txtCustomerName, txtCustomerType, txtMobile, txtEditProfile, txtAddressType, txtCapacity, txtDate, txtTime, txtAddress, txtInstruction, txtAmount, txtConfirmReview;
    private RecyclerView recyclerBill;
    private RadioButton radioPayOnDelivery;
    private RelativeLayout relBack, relConfirmBooking;

    private DataProcessor dataProcessor;

    private BookingInterface bookingInterface;
    private UserInterface userInterface;

    private BookingChargesAdapter bookingChargesAdapter;
    private double amount = 0, price = 0;

    private List<BookingParticularModel> bookingParticularModels = new ArrayList<>();
    private List<ChargeData> chargeDataList = new ArrayList<>();

    private ProgressDialog progressDialog;

    private ContentLoadingProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_booking);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initializeView();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait...");

        dataProcessor = DataProcessor.getInstance(this);
        bookingChargesAdapter = new BookingChargesAdapter(this);
        recyclerBill.setAdapter(bookingChargesAdapter);

        if (getIntent().hasExtra("id")) {
            isEdit = true;
            getSupportActionBar().setTitle("Review Update");
            setUpdateConfig();
        } else {
            isEdit = false;
            getSupportActionBar().setTitle("Confirm Booking");
            setNewConfig();
        }

        bookingInterface = Database.getClient(this).create(BookingInterface.class);
        userInterface = Database.getClient(this).create(UserInterface.class);

        Integer userId = dataProcessor.getInteger(DataProcessor.USER_ID);
        Integer areaId = dataProcessor.getInteger(DataProcessor.SELECTED_ADDRESS_ID);
        String date = getIntent().getStringExtra("date");
        String time = getIntent().getStringExtra("time");
        String address = getIntent().getStringExtra("address");
        String addressType = getIntent().getStringExtra("address_type");
        double lat = getIntent().getDoubleExtra("lat", 0);
        double lng = getIntent().getDoubleExtra("lng", 0);
        String capacity = getIntent().getStringExtra("capacity");
        int capacityId = getIntent().getIntExtra("capacity_id", -1);
        price = getIntent().getDoubleExtra("price", 0);
        String notes = getIntent().getStringExtra("notes");

        String changedDate = changeToDD(date);
        if (changedDate != null)
            txtDate.setText(date);
        else txtDate.setText(changedDate);

        txtTime.setText(time);
        txtAddress.setText(address);
        txtAddressType.setText(addressType);
        txtCapacity.setText(capacity);
        txtInstruction.setText(notes);

        bookingModel = new BookingModel();

        bookingModel.setArea_id(areaId);
        bookingModel.setUser_id(userId);
        bookingModel.setAddress(address);
        bookingModel.setAddress_type(addressType);
        bookingModel.setAmount(amount);
        bookingModel.setCapacity(capacity);
        bookingModel.setDate(date);
        bookingModel.setLat(lat);
        bookingModel.setLng(lng);
        if (TextUtils.isEmpty(notes))
            bookingModel.setNotes(null);
        else bookingModel.setNotes(notes);
        bookingModel.setPrice(price);
        bookingModel.setTime(time);
        if (isEdit) {
            bookingModel.setId(getIntent().getIntExtra("id", -1));
        }
        if (capacityId != -1) {
            getCharges(capacityId);
        }

        txtEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfirmBooking.this, EditProfile.class);
                startActivity(intent);
            }
        });

        getUserDetail(dataProcessor.getInteger(DataProcessor.USER_ID));
        relConfirmBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEdit)
                    updateBooking();
                else bookTanker();
            }
        });

        relBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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

    private void bookTanker() {
        progressDialog.show();
        bookingInterface.bookTanker(bookingModel).enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus().equals("SUCCESS")) {
                        Intent intent = new Intent(ConfirmBooking.this, BookingDetails.class);
                        intent.putExtra("id", response.body().getData().getId());
                        startActivity(intent);
                        finish();
                        dataProcessor.saveBoolean(DataProcessor.PRESS_BACK_BOOK_TANKER,true);
                        Toast.makeText(ConfirmBooking.this, "Tanker booked successfully", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ConfirmBooking.this, "Failed to book Tanker. Try again", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ConfirmBooking.this, "Something went wrong. Try again later" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateBooking() {
        progressDialog.show();
        bookingInterface.modifyBooking(bookingModel).enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus().equals("SUCCESS")) {
                        Intent intent = new Intent(ConfirmBooking.this, BookingDetails.class);
                        intent.putExtra("id", response.body().getData().getId());
                        startActivity(intent);
                        finish();
                        dataProcessor.saveBoolean(DataProcessor.PRESS_BACK_BOOK_TANKER,true);
                        Toast.makeText(ConfirmBooking.this, "Booking updated successfully", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ConfirmBooking.this, "Failed to update Booking. Try again", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ConfirmBooking.this, "Something went wrong. Try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserDetail(Integer userId) {
        userInterface.user(userId).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus().equals("SUCCESS")) {
                        try{
                            UserData userData = response.body().getData();
                            Glide.with(ConfirmBooking.this).load(userData.getImage()).placeholder(R.drawable.ic_profile_placeholder).into(circularProfile);
                            if (userData.getName() == null || TextUtils.isEmpty(userData.getName()))
                                txtCustomerName.setText("-");
                            else txtCustomerName.setText(userData.getName());
                            txtMobile.setText("+91 "+userData.getPhone());
                            if (userData.getCustomerType() == null || TextUtils.isEmpty(userData.getCustomerType()))
                                txtCustomerType.setText("-");
                            else txtCustomerType.setText(userData.getCustomerType());
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

    private void getCharges(Integer capacityId) {
        progress.show();
        bookingInterface.getChargesByCapacityId(capacityId).enqueue(new Callback<ChargeListResponse>() {
            @Override
            public void onResponse(Call<ChargeListResponse> call, Response<ChargeListResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus().equals("SUCCESS")) {
                        chargeDataList.clear();
                        chargeDataList.addAll(response.body().getData());
                        bookingChargesAdapter.setChargeDataList(chargeDataList);
                        calculateSum(response.body().getData());
                    }
                } else {
                    Toast.makeText(ConfirmBooking.this, "Something went wrong. Try again later", Toast.LENGTH_SHORT).show();
                }
                progress.hide();
            }

            @Override
            public void onFailure(Call<ChargeListResponse> call, Throwable t) {
                progress.hide();
                Toast.makeText(ConfirmBooking.this, "Something went wrong. Try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateSum(List<ChargeData> chargeDataList) {
        amount = 0;
        for (ChargeData chargeData : chargeDataList) {
            BookingParticularModel bookingParticularModel = new BookingParticularModel();
            double singleAmount = 0;
            if (TextUtils.equals(chargeData.getType(), "PERCENT")) {
                singleAmount = chargeData.getPercent() * price / 100;
            } else {
                singleAmount = chargeData.getAmount();
            }
            if (chargeData.getCharge_type() == 1) {
                amount -= singleAmount;
            } else {
                amount += singleAmount;
            }
            bookingParticularModel.setTitle(chargeData.getTitle());
            bookingParticularModel.setPrice(singleAmount);
            bookingParticularModel.setType(chargeData.getCharge_type());
            bookingParticularModel.setExtra("");
            bookingParticularModels.add(bookingParticularModel);
        }
        txtAmount.setText("Rs " + amount);
        bookingModel.setAmount(amount);
        bookingModel.setParticulars(bookingParticularModels);
    }

    private void setNewConfig() {
        isEdit = false;
        relConfirmBooking.setBackground(getResources().getDrawable(R.drawable.btn_solid_accent));
        txtConfirmReview.setText("Confirm Review");
        txtConfirmReview.setTextColor(getResources().getColor(R.color.colorWhite));
    }

    private void setUpdateConfig() {
        isEdit = true;
        relConfirmBooking.setBackground(getResources().getDrawable(R.drawable.btn_white_with_border));
        txtConfirmReview.setText("Confirm Update");
        txtConfirmReview.setTextColor(getResources().getColor(R.color.colorAccent));
    }

    private void initializeView() {
        progress = findViewById(R.id.progress);

        circularProfile = findViewById(R.id.circularProfile);
        txtCustomerName = findViewById(R.id.txtCustomerName);
        txtCustomerType = findViewById(R.id.txtCustomerType);
        txtMobile = findViewById(R.id.txtMobile);
        txtEditProfile = findViewById(R.id.txtEditProfile);
        txtAddressType = findViewById(R.id.txtAddressType);
        txtCapacity = findViewById(R.id.txtCapacity);
        txtDate = findViewById(R.id.txtDate);
        txtTime = findViewById(R.id.txtTime);
        txtAddress = findViewById(R.id.txtAddress);
        txtInstruction = findViewById(R.id.txtInstruction);
        txtAmount = findViewById(R.id.txtAmount);
        txtConfirmReview = findViewById(R.id.txtConfirmReview);

        recyclerBill = findViewById(R.id.recyclerBill);
        recyclerBill.setHasFixedSize(true);
        recyclerBill.setLayoutManager(new LinearLayoutManager(this));

        radioPayOnDelivery = findViewById(R.id.radioPayOnDelivery);
        relBack = findViewById(R.id.relBack);
        relConfirmBooking = findViewById(R.id.relConfirmBooking);
    }

    private void setData(){
        try {
            txtCustomerName.setText(dataProcessor.getString(DataProcessor.NAME));
            txtMobile.setText("+91 "+dataProcessor.getString(DataProcessor.PHONE));
            txtCustomerType.setText(dataProcessor.getString(DataProcessor.CUSTOMER_TYPE));
            Glide.with(this).load(dataProcessor.getString(DataProcessor.IMAGE)).placeholder(R.drawable.ic_profile_placeholder).into(circularProfile);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setData();
    }
}