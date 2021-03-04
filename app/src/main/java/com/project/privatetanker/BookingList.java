package com.project.privatetanker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.project.privatetanker.Adapters.BookingAdapter;
import com.project.privatetanker.Database.BookingInterface;
import com.project.privatetanker.Database.Database;
import com.project.privatetanker.Database.UserInterface;
import com.project.privatetanker.Models.Bookings.BookingListResponse;
import com.project.privatetanker.Utils.DataProcessor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingList extends AppCompatActivity implements BookingAdapter.BookingClickEvents{

    private RecyclerView recyclerBookingList;
    private BookingAdapter bookingAdapter;
    private BookingInterface bookingInterface;

    private DataProcessor dataProcessor;

    private ContentLoadingProgressBar progress;
    private TextView txtNoBookings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_list);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        dataProcessor=DataProcessor.getInstance(this);

        recyclerBookingList=findViewById(R.id.recyclerBookingList);
        recyclerBookingList.setHasFixedSize(true);
        recyclerBookingList.setLayoutManager(new LinearLayoutManager(this));

        progress=findViewById(R.id.progress);
        txtNoBookings=findViewById(R.id.txtNoBookings);

        bookingAdapter=new BookingAdapter(this,this);
        recyclerBookingList.setAdapter(bookingAdapter);

        bookingInterface= Database.getClient(this).create(BookingInterface.class);
        getBookings();
    }

    private void getBookings(){
        progress.show();
        bookingInterface.getBookingList(dataProcessor.getInteger(DataProcessor.USER_ID)).enqueue(new Callback<BookingListResponse>() {
            @Override
            public void onResponse(Call<BookingListResponse> call, Response<BookingListResponse> response) {
                if (response.isSuccessful()){
                    if (response.body() != null && response.body().getStatus().equals("SUCCESS")) {
                        if (response.body().getData()!=null&&response.body().getData().size()!=0){
                            bookingAdapter.setBookingList(response.body().getData());
                            recyclerBookingList.setVisibility(View.VISIBLE);
                            txtNoBookings.setVisibility(View.GONE);
                        }else {
                            recyclerBookingList.setVisibility(View.GONE);
                            txtNoBookings.setVisibility(View.VISIBLE);
                        }
                    }
                }
                progress.hide();
            }

            @Override
            public void onFailure(Call<BookingListResponse> call, Throwable t) {
                progress.hide();
            }
        });
    }

    @Override
    public void onBookingClick(Integer bookingId) {
        Intent intent=new Intent(BookingList.this,BookingDetails.class);
        intent.putExtra("id",bookingId);
        startActivity(intent);
    }
}