package com.project.privatetanker.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.privatetanker.Models.BookingParticular.BookingParticularData;
import com.project.privatetanker.R;

import java.util.ArrayList;
import java.util.List;

public class BookingParticularAdapter extends RecyclerView.Adapter<BookingParticularAdapter.BookingParticularViewHolder> {

    private Context mCxt;
    private List<BookingParticularData>bookingParticularDataList=new ArrayList<>();

    public BookingParticularAdapter(Context mCxt) {
        this.mCxt = mCxt;
    }

    public void setBookingParticularList(List<BookingParticularData>bookingParticularList){
        bookingParticularDataList=bookingParticularList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookingParticularViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mCxt).inflate(R.layout.item_booking_bill,parent,false);
        return new BookingParticularViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingParticularViewHolder holder, int position) {
        BookingParticularData bookingParticularData=bookingParticularDataList.get(position);
        holder.txtTitle.setText(bookingParticularData.getTitle());
        if (bookingParticularData.getType()==1)
        holder.txtAmount.setText("-Rs "+bookingParticularData.getPrice());
        else holder.txtAmount.setText("+Rs "+bookingParticularData.getPrice());
    }

    @Override
    public int getItemCount() {
        return bookingParticularDataList.size();
    }

    public class BookingParticularViewHolder extends RecyclerView.ViewHolder{
        private TextView txtTitle,txtAmount;
        public BookingParticularViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle=itemView.findViewById(R.id.txtTitle);
            txtAmount=itemView.findViewById(R.id.txtAmount);
        }
    }
}
