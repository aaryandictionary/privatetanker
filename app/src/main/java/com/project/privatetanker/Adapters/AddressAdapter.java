package com.project.privatetanker.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.privatetanker.Models.Address.AddressData;
import com.project.privatetanker.R;

import java.util.ArrayList;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {
    private Context mCxt;
    private List<AddressData>addressDataList=new ArrayList<>();
    private AddressClickEvent addressClickEvent;

    private int lastSelectedPosition=-1;
    private int selectedAddressId=-1;

    public AddressAdapter(Context mCxt, AddressClickEvent addressClickEvent) {
        this.mCxt = mCxt;
        this.addressClickEvent = addressClickEvent;
    }

    public void setAddressList(List<AddressData>addressList,int selectedId){
        addressDataList=addressList;
        selectedAddressId=selectedId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mCxt).inflate(R.layout.item_select_location,parent,false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        AddressData addressData=addressDataList.get(position);
        holder.txtAddress.setText(addressData.getAddress());

        if (position==lastSelectedPosition){
            holder.relAddress.setBackground(mCxt.getResources().getDrawable(R.drawable.bg_location_selected));
        }else {
            if (lastSelectedPosition==-1&&selectedAddressId==addressData.getId()){
                holder.relAddress.setBackground(mCxt.getResources().getDrawable(R.drawable.bg_location_selected));
            }else {
                holder.relAddress.setBackground(mCxt.getResources().getDrawable(R.drawable.bg_location_unselected));
            }
        }
    }

    @Override
    public int getItemCount() {
        return addressDataList.size();
    }

    public class AddressViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout relAddress;
        TextView txtAddress;
        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            relAddress=itemView.findViewById(R.id.relAddress);
            txtAddress=itemView.findViewById(R.id.txtAddress);
            relAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition()!=-1&&getAdapterPosition()<addressDataList.size()){
                        lastSelectedPosition=getAdapterPosition();
                        addressClickEvent.onAddressClick(addressDataList.get(getAdapterPosition()).getId());
                        notifyDataSetChanged();
                    }
                }
            });
        }
    }

    public interface AddressClickEvent{
        void onAddressClick(Integer id);
    }
}
