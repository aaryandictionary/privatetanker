package com.project.privatetanker.Adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.privatetanker.Models.Charges.ChargeData;
import com.project.privatetanker.R;

import java.util.ArrayList;
import java.util.List;

public class BookingChargesAdapter extends RecyclerView.Adapter<BookingChargesAdapter.BookingChargesViewHolder> {

    private Context mCxt;
    private List<ChargeData>chargeDataList=new ArrayList<>();
    private double price=0;

    public BookingChargesAdapter(Context mCxt) {
        this.mCxt = mCxt;
    }

    public void setChargeDataList(List<ChargeData>chargeData){
        chargeDataList=chargeData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookingChargesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mCxt).inflate(R.layout.item_booking_bill,parent,false);
        return new BookingChargesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingChargesViewHolder holder, int position) {
        ChargeData chargeData=chargeDataList.get(position);
        holder.txtTitle.setText(chargeData.getTitle());
        Double singlePrice=0.0;
        if (TextUtils.equals(chargeData.getType(),"PERCENT")){
            singlePrice=chargeData.getPercent()*price/100;
        }else {
            singlePrice=chargeData.getAmount();
        }
        if (chargeData.getCharge_type()==1){
            holder.txtAmount.setText("- Rs "+singlePrice);
        }else {
            holder.txtAmount.setText("+ Rs "+singlePrice);
        }
    }

    @Override
    public int getItemCount() {
        return chargeDataList.size();
    }

    public class BookingChargesViewHolder extends RecyclerView.ViewHolder{
        private TextView txtTitle,txtAmount;
        public BookingChargesViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle=itemView.findViewById(R.id.txtTitle);
            txtAmount=itemView.findViewById(R.id.txtAmount);
        }
    }
}
