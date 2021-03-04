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

public class UserAddressAdapter extends RecyclerView.Adapter<UserAddressAdapter.UserAddressViewHolder> {

    private Context mCxt;
    private List<AddressData>addressDataList=new ArrayList<>();
    private UserAddressClickEvent addressClickEvent;

    public UserAddressAdapter(Context mCxt, UserAddressClickEvent addressClickEvent) {
        this.mCxt = mCxt;
        this.addressClickEvent = addressClickEvent;
    }

    public void setAddressList(List<AddressData>addressList){
        addressDataList=addressList;
        notifyDataSetChanged();
    }

    public void updateAddress(AddressData addressData,int position){
        addressDataList.set(position,addressData);
        notifyItemChanged(position);
    }

    public void addAddress(AddressData addressData){
        addressDataList.add(addressData);
        notifyItemInserted(addressDataList.size());
    }

    @NonNull
    @Override
    public UserAddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mCxt).inflate(R.layout.item_address,parent,false);
        return new UserAddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAddressViewHolder holder, int position) {
        AddressData addressData=addressDataList.get(position);
        holder.txtAddress.setText(addressData.getAddress());
        holder.txtAddressType.setText(addressData.getAddressType());

        holder.relAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressClickEvent.onUserAddressClick(addressData,addressData.getId(),position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressDataList.size();
    }

    public class UserAddressViewHolder extends RecyclerView.ViewHolder{
        private TextView txtAddress,txtAddressType;
        private RelativeLayout relAddress;
        public UserAddressViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAddress=itemView.findViewById(R.id.txtAddress);
            txtAddressType=itemView.findViewById(R.id.txtAddressType);
            relAddress=itemView.findViewById(R.id.relAddress);
        }
    }

    public interface  UserAddressClickEvent{
        void onUserAddressClick(AddressData addressData,Integer id,int position);
    }
}
