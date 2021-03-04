package com.project.privatetanker.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.project.privatetanker.Adapters.AddressAdapter;
import com.project.privatetanker.Database.Database;
import com.project.privatetanker.Database.UserInterface;
import com.project.privatetanker.MapActivity;
import com.project.privatetanker.Models.Address.AddressListResponse;
import com.project.privatetanker.Models.Address.AddressResponse;
import com.project.privatetanker.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BottomSheetAddress extends BottomSheetDialogFragment implements AddressAdapter.AddressClickEvent{

    private AddressAdapter addressAdapter;
    private DataProcessor dataProcessor;
    private int selectedAddressId=-1;

    private BottomSheetAddressListener bottomSheetAddressListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.bottom_sheet_select_location,container,false);
        RecyclerView recyclerAddresses=view.findViewById(R.id.recyclerAddresses);
        recyclerAddresses.setHasFixedSize(true);
        recyclerAddresses.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        TextView txtAddNew=view.findViewById(R.id.txtAddNew);

        dataProcessor=DataProcessor.getInstance(getContext());
        addressAdapter=new AddressAdapter(getActivity(),this);
        recyclerAddresses.setAdapter(addressAdapter);

        getAddressList();

        RelativeLayout relSelectAddress=view.findViewById(R.id.relSelectAddress);

        txtAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Intent intent=new Intent(getContext(), MapActivity.class);
                startActivity(intent);
            }
        });

        relSelectAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                dataProcessor.saveInteger(DataProcessor.SELECTED_ADDRESS_ID,selectedAddressId);
                bottomSheetAddressListener.onAddressSelect(selectedAddressId);
            }
        });

        return view;
    }

    private void getAddressList(){
        UserInterface userInterface= Database.getClient(getContext()).create(UserInterface.class);
        userInterface.addresses(dataProcessor.getInteger(DataProcessor.USER_ID)).enqueue(new Callback<AddressListResponse>() {
            @Override
            public void onResponse(Call<AddressListResponse> call, Response<AddressListResponse> response) {
                if (response.isSuccessful()){
                    assert response.body() != null;
                    addressAdapter.setAddressList(response.body().getData(),dataProcessor.getInteger(DataProcessor.SELECTED_ADDRESS_ID));
                }
            }

            @Override
            public void onFailure(Call<AddressListResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onAddressClick(Integer id) {
        selectedAddressId=id;
    }

    public interface BottomSheetAddressListener{
        void onAddressSelect(Integer addressId);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            bottomSheetAddressListener = (BottomSheetAddressListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement BottomSheetListener");
        }
    }
}
