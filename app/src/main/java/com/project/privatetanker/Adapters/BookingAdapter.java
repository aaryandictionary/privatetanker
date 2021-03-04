package com.project.privatetanker.Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.privatetanker.Models.Bookings.BookingData;
import com.project.privatetanker.R;
import com.project.privatetanker.Utils.DataProcessor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private Context mCxt;
    private List<BookingData>bookingDataList=new ArrayList<>();
    private BookingClickEvents bookingClickEvents;
    private DataProcessor dataProcessor;

    public BookingAdapter(Context mCxt, BookingClickEvents bookingClickEvents) {
        this.mCxt = mCxt;
        dataProcessor=DataProcessor.getInstance(mCxt);
        this.bookingClickEvents = bookingClickEvents;
    }

    public void setBookingList(List<BookingData>bookingList){
        bookingDataList=bookingList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mCxt).inflate(R.layout.item_order_item,parent,false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        BookingData bookingData=bookingDataList.get(position);
        String date=changeToDD(bookingData.getDate());
        if (date!=null)
        holder.txtDate.setText(date);
        else holder.txtDate.setText(bookingData.getDate());
        holder.txtTime.setText(bookingData.getTime());
        holder.txtStatus.setText(bookingData.getStatus().toUpperCase().charAt(0)+bookingData.getStatus().toLowerCase().substring(1));
        holder.txtCapacity.setText(bookingData.getCapacity());
        holder.txtAddressType.setText(bookingData.getAddress());
        holder.txtAddress.setText(bookingData.getAddress());
        holder.txtPrice.setText("Rs "+bookingData.getPrice());

        if (TextUtils.equals(bookingData.getStatus(),"PROCESSING"))
            holder.txtStatus.setTextColor(mCxt.getResources().getColor(R.color.colorYellowDark));
        else if (TextUtils.equals(bookingData.getStatus(),"CANCELLED"))
            holder.txtStatus.setTextColor(mCxt.getResources().getColor(R.color.colorRed));
        else if (TextUtils.equals(bookingData.getStatus(),"COMPLETED"))
            holder.txtStatus.setTextColor(mCxt.getResources().getColor(R.color.colorGreen));

        if (bookingData.getUserId()!=dataProcessor.getInteger(DataProcessor.USER_ID)){
            holder.txtName.setVisibility(View.VISIBLE);
            holder.txtName.setText(bookingData.getUserName());
        }else {
            holder.txtName.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return bookingDataList.size();
    }

    public class BookingViewHolder extends RecyclerView.ViewHolder{
        private RelativeLayout relBooking;
        private TextView txtAddress,txtPrice,txtAddressType,txtCapacity,txtDate,txtTime,txtStatus,txtName;
        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            relBooking=itemView.findViewById(R.id.relBooking);
            txtAddress=itemView.findViewById(R.id.txtAddress);
            txtPrice=itemView.findViewById(R.id.txtPrice);
            txtAddressType=itemView.findViewById(R.id.txtAddressType);
            txtCapacity=itemView.findViewById(R.id.txtCapacity);
            txtDate=itemView.findViewById(R.id.txtDate);
            txtTime=itemView.findViewById(R.id.txtTime);
            txtStatus=itemView.findViewById(R.id.txtStatus);
            txtName=itemView.findViewById(R.id.txtName);

            relBooking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition()!=-1&&getAdapterPosition()<bookingDataList.size())
                    bookingClickEvents.onBookingClick(bookingDataList.get(getAdapterPosition()).getId());
                }
            });
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

    public interface BookingClickEvents{
        void onBookingClick(Integer bookingId);
    }
}
