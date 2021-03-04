package com.project.privatetanker.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.project.privatetanker.Models.Connections.ConnectionData;
import com.project.privatetanker.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context mCxt;
    private List<ConnectionData>connectionDataList=new ArrayList<>();
    private UserClickEvents userClickEvents;

    public UserAdapter(Context mCxt, UserClickEvents userClickEvents) {
        this.mCxt = mCxt;
        this.userClickEvents = userClickEvents;
    }

    public void setUserList(List<ConnectionData>connectionData){
        connectionDataList=connectionData;
        notifyDataSetChanged();
    }

    public void addUser(ConnectionData connectionData){
        Integer pos=connectionDataList.size();
        connectionDataList.add(connectionData);
        notifyItemInserted(pos);
    }

    public void removeUser(Integer pos){
        connectionDataList.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos,connectionDataList.size()-pos);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mCxt).inflate(R.layout.item_user,parent,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        ConnectionData connectionData=connectionDataList.get(position);
        holder.txtName.setText(connectionData.getName());
        holder.txtMobile.setText("+91 "+connectionData.getPhone());
        Glide.with(mCxt).load(connectionData.getImage()).placeholder(R.drawable.ic_profile_placeholder).into(holder.circularProfile);
    }

    @Override
    public int getItemCount() {
        return connectionDataList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView circularProfile;
        private RelativeLayout relUser;
        private TextView txtName,txtMobile;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            circularProfile=itemView.findViewById(R.id.circularProfile);
            relUser=itemView.findViewById(R.id.relUser);
            txtName=itemView.findViewById(R.id.txtName);
            txtMobile=itemView.findViewById(R.id.txtMobile);

            relUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition()!=-1&&getAdapterPosition()<connectionDataList.size()){
                        userClickEvents.onUserClick(connectionDataList.get(getAdapterPosition()),getAdapterPosition());
                    }
                }
            });
        }
    }

    public interface UserClickEvents{
        void onUserClick(ConnectionData connectionData,Integer position);
    }
}
