package com.project.privatetanker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.snackbar.Snackbar;
import com.project.privatetanker.Adapters.CapacityAdapter;
import com.project.privatetanker.Database.BookingInterface;
import com.project.privatetanker.Database.Database;
import com.project.privatetanker.Database.UserInterface;
import com.project.privatetanker.Models.Address.AddressData;
import com.project.privatetanker.Models.Address.AddressResponse;
import com.project.privatetanker.Models.Address.CheckLocationModel;
import com.project.privatetanker.Models.Address.CheckLocationResponse;
import com.project.privatetanker.Models.Bookings.BookingData;
import com.project.privatetanker.Models.Bookings.BookingResponse;
import com.project.privatetanker.Models.Capacity.CapacityData;
import com.project.privatetanker.Models.Capacity.CapacityListResponse;
import com.project.privatetanker.Utils.BottomSheetAddress;
import com.project.privatetanker.Utils.DataProcessor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookTanker extends AppCompatActivity implements CapacityAdapter.CapacityClickEvents,BottomSheetAddress.BottomSheetAddressListener{

    private RelativeLayout relReview,relBack;
    private RecyclerView recyclerCapacities;
    private TextView txtAddress,txtChangeAddress,txtDate,txtTime,txtPrice,txtReview;
    private LinearLayout linSelectDate,linSelectTime;
    private EditText textNotes;

    private DataProcessor dataProcessor;
    private UserInterface userInterface;
    private BookingInterface bookingInterface;

    private CapacityAdapter capacityAdapter;

    private BottomSheetAddress bottomSheetAddress;

    private AddressData addressData;
    final Calendar myCalendar = Calendar.getInstance();

    private boolean isEdit=false;
    private String date,time,capacity="";
    private double price;
    private Integer areaId=-1,capacityId=-1,id=-1;
    private TimePickerDialog timePickerDialog;

    private ProgressDialog progressDialog;
    private ContentLoadingProgressBar progress;

    private Integer bookingId=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_tanker);
        initializeView();

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        progressDialog=new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait...");

        dataProcessor=DataProcessor.getInstance(this);
        userInterface= Database.getClient(this).create(UserInterface.class);
        bookingInterface=Database.getClient(this).create(BookingInterface.class);

        capacityAdapter=new CapacityAdapter(this,this);
        recyclerCapacities.setAdapter(capacityAdapter);

//        areaId=dataProcessor.getInteger(DataProcessor.SELECTED_AREA_ID);
        int addressId=dataProcessor.getInteger(DataProcessor.SELECTED_ADDRESS_ID);

//        date=getDate();
        time=getTime()+":00";
        txtDate.setText(getDate());
        txtTime.setText(time);

        bottomSheetAddress=new BottomSheetAddress();
        bottomSheetAddress.setCancelable(true);

        if (getIntent().hasExtra("id")){
            setUpdateConfig();
            id=getIntent().getIntExtra("id",-1);
            bookingId=id;
            getBookingDetails();
            getSupportActionBar().setTitle("Update Booking");
        }else {
            if (getIntent().hasExtra("bookingId")){
                bookingId=getIntent().getIntExtra("bookingId",-1);
                getBookingDetails();
            }else {
                if (addressId!=-1){
                    getAddressDetails(addressId);
                }
            }
            setNewConfig();
            getSupportActionBar().setTitle("Book Tanker");
        }

        relReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(BookTanker.this,ConfirmBooking.class);
                intent.putExtra("date",date);
                intent.putExtra("time",time);
                intent.putExtra("address",txtAddress.getText().toString());
                intent.putExtra("address_type",addressData.getAddressType());
                intent.putExtra("lat",addressData.getLat());
                intent.putExtra("lng",addressData.getLng());
                intent.putExtra("capacity",capacity);
                intent.putExtra("capacity_id",capacityId);
                intent.putExtra("price",price);
                intent.putExtra("notes",textNotes.getText().toString());
                if (isEdit){
                    intent.putExtra("id",id);
                }
                startActivity(intent);
                dataProcessor.saveBoolean(DataProcessor.PRESS_BACK_BOOK_TANKER,false);
            }
        });

        relBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        linSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(BookTanker.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                myCalendar.set(Calendar.YEAR, year);
                                myCalendar.set(Calendar.MONTH, monthOfYear);
                                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                                SimpleDateFormat sdfS = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                date=sdfS.format(myCalendar.getTime());
                                txtDate.setText(sdf.format(myCalendar.getTime()));
                            }
                        },myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        linSelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar=Calendar.getInstance();

                timePickerDialog = new TimePickerDialog(
                        BookTanker.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String hrs="",min="";
                                if (hourOfDay<10)
                                    hrs="0"+hourOfDay;
                                else hrs=""+hourOfDay;
                                if (minute<10)
                                    min="0"+minute;
                                else min=""+minute;
                                time=hrs+":"+min+":00";
                                txtTime.setText(time);
                            }
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true);

                timePickerDialog.show();
            }
        });

        txtChangeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetAddress.show(getSupportFragmentManager(),"bottomSheetAddress");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dataProcessor.getBoolean(DataProcessor.PRESS_BACK_BOOK_TANKER)){
            dataProcessor.saveBoolean(DataProcessor.PRESS_BACK_BOOK_TANKER,false);
            onBackPressed();
        }
    }

    private String getDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat sdfS = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());
        date=sdfS.format(new Date(System.currentTimeMillis()));
        return sdf.format(new Date(System.currentTimeMillis()));
    }

    private String getTime(){
        String dateFormat="HH:mm";
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.getDefault());
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter.format(new Date(System.currentTimeMillis()+7200000));
    }

    private void setNewConfig(){
        isEdit=false;
        relReview.setBackground(getResources().getDrawable(R.drawable.btn_solid_accent));
        txtReview.setText("Review");
        txtReview.setTextColor(getResources().getColor(R.color.colorWhite));
    }

    private void setUpdateConfig(){
        isEdit=true;
        relReview.setBackground(getResources().getDrawable(R.drawable.btn_white_with_border));
        txtReview.setText("Review Update");
        txtReview.setTextColor(getResources().getColor(R.color.colorAccent));
    }

    private void getAddressDetails(Integer addressId){
        progressDialog.show();
        userInterface.getAddressById(addressId).enqueue(new Callback<AddressResponse>() {
            @Override
            public void onResponse(Call<AddressResponse> call, Response<AddressResponse> response) {
                if (response.isSuccessful()){
                    if (response.body() != null && response.body().getStatus().equals("SUCCESS")) {
                        addressData = response.body().getData();
                        txtAddress.setText(addressData.getAddress());
                        checkAreaService(response.body().getData().getLat(), response.body().getData().getLng());
                    }
                }
            }

            @Override
            public void onFailure(Call<AddressResponse> call, Throwable t) {

            }
        });
    }

    private void checkAreaService(double lat,double lng){
        CheckLocationModel checkLocationModel=new CheckLocationModel();
        checkLocationModel.setLat(lat);
        checkLocationModel.setLng(lng);
        userInterface.checkServiceable(checkLocationModel).enqueue(new Callback<CheckLocationResponse>() {
            @Override
            public void onResponse(Call<CheckLocationResponse> call, Response<CheckLocationResponse> response) {
                if (response.isSuccessful()){
                    try {
                        if (response.body() != null && response.body().getStatus().equals("SUCCESS")) {
                            if (response.body().getData() != null) {
                                areaId = Integer.parseInt(response.body().getData().getId());
                                dataProcessor.saveInteger(DataProcessor.SELECTED_AREA_ID, areaId);
                                getCapacities(areaId);
                            } else {
                                Intent intent = new Intent(BookTanker.this, UnserviceableLocation.class);
                                startActivity(intent);
                            }
                        }
                        progressDialog.dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<CheckLocationResponse> call, Throwable t) {

            }
        });
    }

    private void  getCapacities(Integer areaId){
        progress.show();
        bookingInterface.getTankerCapacity(areaId).enqueue(new Callback<CapacityListResponse>() {
            @Override
            public void onResponse(Call<CapacityListResponse> call, Response<CapacityListResponse> response) {
                if (response.isSuccessful()){
                    if (response.body() != null && response.body().getStatus().equals("SUCCESS")) {
                        capacityAdapter.setCapacities(response.body().getData());
                        if (response.body().getData().size() != 0) {
                            CapacityData capacityData = response.body().getData().get(0);
                            if (bookingId != -1) {
                                if (capacity!=null&&!TextUtils.isEmpty(capacity)){
                                    boolean isSet= capacityAdapter.setCapacityForReorder(capacity);
                                    if(!isSet){
                                        showSnackBar("Selected capacity is not available");
                                        capacity = capacityData.getCapacity();
                                        capacityId = capacityData.getId();
                                        price = capacityData.getPrice();
                                        txtPrice.setText("Rs " + capacityData.getPrice());
                                    }
                                }else {
                                    capacity = capacityData.getCapacity();
                                    capacityId = capacityData.getId();
                                    price = capacityData.getPrice();
                                    txtPrice.setText("Rs " + capacityData.getPrice());
                                }
                            }else {
                                capacity = capacityData.getCapacity();
                                capacityId = capacityData.getId();
                                price = capacityData.getPrice();
                                txtPrice.setText("Rs " + capacityData.getPrice());
                            }
                        } else {
                            openNoCapacityDialog();
                        }
                    }
                }
                progress.hide();
            }

            @Override
            public void onFailure(Call<CapacityListResponse> call, Throwable t) {
                progress.hide();
            }
        });
    }

    private void openNoCapacityDialog(){
        AlertDialog alertDialog=new AlertDialog.Builder(this).create();
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_permission_required,null);
        alertDialog.setView(view);
        alertDialog.setCanceledOnTouchOutside(false);

        TextView instruction=view.findViewById(R.id.instruction);
        RelativeLayout relRequestPermission=view.findViewById(R.id.relRequestPermission);
        TextView txtButtonTitle=view.findViewById(R.id.txtButtonTitle);

        instruction.setText("No Tanker found for the selected location. Change your location or contact support");
        txtButtonTitle.setText("Change Location");
        relRequestPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                bottomSheetAddress.show(getSupportFragmentManager(),"bottomSheetAddress");
            }
        });
        alertDialog.show();
    }

    private void getBookingDetails(){
        progressDialog.show();
        bookingInterface.getBookingDetails(bookingId).enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                if (response.isSuccessful()){
                    if (response.body() != null && response.body().getStatus().equals("SUCCESS")) {
                        setBookingDetails(response.body().getData());
                        checkAreaService(response.body().getData().getLat(),response.body().getData().getLng());
                    }
                }
            }

            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {

            }
        });
    }

    private void setBookingDetails(BookingData bookingData){
        if (bookingData!=null){
            if (id!=-1){
                txtDate.setText(changeToDD(bookingData.getDate()));
                date=bookingData.getDate();
                time=bookingData.getTime();
                txtTime.setText(bookingData.getTime());
            }
            if (addressData==null)
                addressData=new AddressData();
            txtAddress.setText(bookingData.getAddress());
            addressData.setAddress(bookingData.getAddress());
            addressData.setAddressType(bookingData.getAddress_type());
            addressData.setLat(bookingData.getLat());
            addressData.setLng(bookingData.getLng());
            textNotes.setText(bookingData.getNotes());
            capacity=bookingData.getCapacity();
        }
    }

    private String changeToDD(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat sdfS = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            return sdf.format(sdfS.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void showSnackBar(String message){
        Snackbar.make(findViewById(android.R.id.content),message,Snackbar.LENGTH_SHORT).show();
    }

    private void initializeView(){
        relReview=findViewById(R.id.relReview);
        relBack=findViewById(R.id.relBack);
        recyclerCapacities=findViewById(R.id.recyclerCapacities);
        recyclerCapacities.setHasFixedSize(true);
        recyclerCapacities.setLayoutManager(new GridLayoutManager(this,3));

        txtReview=findViewById(R.id.txtReview);
        txtAddress=findViewById(R.id.txtAddress);
        txtChangeAddress=findViewById(R.id.txtChangeAddress);
        txtDate=findViewById(R.id.txtDate);
        txtTime=findViewById(R.id.txtTime);
        txtPrice=findViewById(R.id.txtPrice);
        linSelectDate=findViewById(R.id.linSelectDate);
        linSelectTime=findViewById(R.id.linSelectTime);
        textNotes=findViewById(R.id.textNotes);
        progress=findViewById(R.id.progress);
    }

    @Override
    public void onCapacityClick(CapacityData capacityData) {
        capacity=capacityData.getCapacity();
        capacityId=capacityData.getId();
        price=capacityData.getPrice();
        txtPrice.setText("Rs "+capacityData.getPrice());
    }

    @Override
    public void onAddressSelect(Integer addressId) {
        getAddressDetails(addressId);
    }
}