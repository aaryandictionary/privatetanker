package com.project.privatetanker.Adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.privatetanker.Models.Capacity.CapacityData;
import com.project.privatetanker.R;

import java.util.ArrayList;
import java.util.List;

public class CapacityAdapter extends RecyclerView.Adapter<CapacityAdapter.CapacityViewHolder> {

    private Context mCxt;
    private List<CapacityData>capacityDataList=new ArrayList<>();
    private CapacityClickEvents capacityClickEvents;

    private Integer lastSelectedCapacity=0;

    public CapacityAdapter(Context mCxt, CapacityClickEvents capacityClickEvents) {
        this.mCxt = mCxt;
        this.capacityClickEvents = capacityClickEvents;
    }

    public void setCapacities(List<CapacityData>capacities){
        capacityDataList=capacities;
        notifyDataSetChanged();
    }

    public boolean setCapacityForReorder(String capacity){
        Integer pos=checkCapacity(capacity);
        if (pos!=-1){
            lastSelectedCapacity=pos;
            notifyDataSetChanged();
            capacityClickEvents.onCapacityClick(capacityDataList.get(pos));
            return true;
        }
        return false;
    }

    private Integer checkCapacity(String capacity){
        for (CapacityData capacityData:capacityDataList){
            if (TextUtils.equals(capacityData.getCapacity(),capacity)){
                return capacityDataList.indexOf(capacityData);
            }
        }
        return -1;
    }

    @NonNull
    @Override
    public CapacityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mCxt).inflate(R.layout.item_select_tanker_capacity,parent,false);
        return new CapacityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CapacityViewHolder holder, int position) {
        CapacityData capacityData=capacityDataList.get(position);
        holder.txtCapacity.setText(capacityData.getCapacity());

        if (lastSelectedCapacity==position){
            holder.relCapacity.setBackground(mCxt.getResources().getDrawable(R.drawable.bg_checked_capacity));
            holder.txtCapacity.setTextColor(mCxt.getResources().getColor(R.color.colorWhite));
        }else {
            holder.relCapacity.setBackground(mCxt.getResources().getDrawable(R.drawable.bg_unchecked_capacity));
            holder.txtCapacity.setTextColor(mCxt.getResources().getColor(R.color.colorAccent));
        }
    }

    @Override
    public int getItemCount() {
        return capacityDataList.size();
    }

    public class CapacityViewHolder extends RecyclerView.ViewHolder{
        private RelativeLayout relCapacity;
        private TextView txtCapacity;
        public CapacityViewHolder(@NonNull View itemView) {
            super(itemView);
            relCapacity=itemView.findViewById(R.id.relCapacity);
            txtCapacity=itemView.findViewById(R.id.txtCapacity);

            relCapacity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition()!=-1&&getAdapterPosition()<capacityDataList.size()) {
                        lastSelectedCapacity=getAdapterPosition();
//                        Toast.makeText(mCxt,"Size "+capacityDataList.size()+" POS "+getAdapterPosition(),Toast.LENGTH_LONG).show();
                        capacityClickEvents.onCapacityClick(capacityDataList.get(getAdapterPosition()));
                        notifyDataSetChanged();
                    }
                }
            });
        }
    }

    public interface CapacityClickEvents{
        void onCapacityClick(CapacityData capacityData);
    }
}
