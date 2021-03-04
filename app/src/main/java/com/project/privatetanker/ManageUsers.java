package com.project.privatetanker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.project.privatetanker.Adapters.UserAdapter;
import com.project.privatetanker.Database.Database;
import com.project.privatetanker.Database.UserInterface;
import com.project.privatetanker.Models.Connections.AddConnectionModel;
import com.project.privatetanker.Models.Connections.AddConnectionResponse;
import com.project.privatetanker.Models.Connections.ConnectionData;
import com.project.privatetanker.Models.Connections.ConnectionResponse;
import com.project.privatetanker.Models.Connections.RemoveConnectionModel;
import com.project.privatetanker.Utils.DataProcessor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageUsers extends AppCompatActivity implements UserAdapter.UserClickEvents, Toolbar.OnMenuItemClickListener {

    private RecyclerView recyclerUsers;

    private UserAdapter userAdapter;
    private UserInterface userInterface;

    private DataProcessor dataProcessor;

    private AlertDialog alertDialog;

    private ContentLoadingProgressBar progress;
    private TextView txtNoUser;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initializeView();

        toolbar.setOnMenuItemClickListener(this);

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
        userAdapter=new UserAdapter(this,this);
        userInterface= Database.getClient(this).create(UserInterface.class);
        recyclerUsers.setAdapter(userAdapter);

        getConnections();
    }

    private void openAddUserDialog(){
        alertDialog=new AlertDialog.Builder(this).create();
        View view= LayoutInflater.from(this).inflate(R.layout.dialog_add_user,null);
        alertDialog.setView(view);

        EditText textMobile=view.findViewById(R.id.textMobile);
        RelativeLayout relAddUser=view.findViewById(R.id.relAddUser);

        relAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(textMobile.getText().toString())&&textMobile.getText().length()==10){
                    AddConnectionModel addConnectionModel=new AddConnectionModel();
                    addConnectionModel.setPhone(textMobile.getText().toString());
                    addConnectionModel.setUser_id(dataProcessor.getInteger(DataProcessor.USER_ID));
                    addConnectionModel.setPending("0");

                    addUser(addConnectionModel);
                }
            }
        });
        alertDialog.show();
    }

    private void addUser(AddConnectionModel addConnectionModel){
        progressDialog.show();
        userInterface.addConnection(addConnectionModel).enqueue(new Callback<AddConnectionResponse>() {
            @Override
            public void onResponse(Call<AddConnectionResponse> call, Response<AddConnectionResponse> response) {
                if (response.isSuccessful()){
                    if (response.body() != null && response.body().getStatus().equals("SUCCESS")) {
                        if (response.body().getCode().equals("SC_01")){
                            Toast.makeText(ManageUsers.this, "User added successfully", Toast.LENGTH_SHORT).show();
                            userAdapter.addUser(response.body().getData());
                        }
                        alertDialog.dismiss();
                    }else {
                        if (response.body() != null) {
                            if (response.body().getCode().equals("FC_02")){
                                Toast.makeText(ManageUsers.this, "User doesn't exist", Toast.LENGTH_SHORT).show();
                            }else  if (response.body().getCode().equals("FC_03")){
                                Toast.makeText(ManageUsers.this, "You cannot add yourself", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(ManageUsers.this,"Something went wrong. Try again",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }else {
                    Toast.makeText(ManageUsers.this,"Something went wrong. Try again",Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<AddConnectionResponse> call, Throwable t) {
                Toast.makeText(ManageUsers.this,"Something went wrong. Try again",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void getConnections(){
        progress.show();
        userInterface.connections(dataProcessor.getInteger(DataProcessor.USER_ID)).enqueue(new Callback<ConnectionResponse>() {
            @Override
            public void onResponse(Call<ConnectionResponse> call, Response<ConnectionResponse> response) {
                if (response.isSuccessful()){
                    if (response.body() != null && response.body().getStatus().equals("SUCCESS")) {
                        if (response.body().getData().size() != 0) {
                            userAdapter.setUserList(response.body().getData());
                            recyclerUsers.setVisibility(View.VISIBLE);
                            txtNoUser.setVisibility(View.GONE);
                        } else {
                            recyclerUsers.setVisibility(View.GONE);
                            txtNoUser.setVisibility(View.VISIBLE);
                        }
                    }
                    progress.hide();
                }
            }

            @Override
            public void onFailure(Call<ConnectionResponse> call, Throwable t) {
                progress.hide();
            }
        });
    }

    private void initializeView(){
        recyclerUsers=findViewById(R.id.recyclerUsers);
        recyclerUsers.setHasFixedSize(true);
        recyclerUsers.setLayoutManager(new LinearLayoutManager(this));

        progress=findViewById(R.id.progress);
        txtNoUser=findViewById(R.id.txtNoUser);
    }

    @Override
    public void onUserClick(ConnectionData connectionData, Integer position) {
        getOptions(connectionData,position);
    }

    private void getOptions(ConnectionData connectionData,Integer position){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ManageUsers.this,R.style.MyDialogTheme);
        String[] arr=new String[1];
        arr[0]="Delete User";
        builder.setItems(arr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    removeConnection(connectionData,position);
                }
            }
        });
        builder.setCancelable(true);
        builder.show();
    }

    private void removeConnection(ConnectionData connectionData,Integer position){
        progressDialog.show();
        RemoveConnectionModel removeConnectionModel=new RemoveConnectionModel();
        removeConnectionModel.setUser_id(dataProcessor.getInteger(DataProcessor.USER_ID));
        removeConnectionModel.setConnected_id(connectionData.getId());
        removeConnectionModel.setPending(connectionData.getPending());
        userInterface.removeConnection(removeConnectionModel).enqueue(new Callback<ConnectionResponse>() {
            @Override
            public void onResponse(Call<ConnectionResponse> call, Response<ConnectionResponse> response) {
                if (response.isSuccessful()){
                    if (response.body() != null && response.body().getStatus().equals("SUCCESS")) {
                        Toast.makeText(ManageUsers.this, "User removed successfully", Toast.LENGTH_SHORT).show();
                        userAdapter.removeUser(position);
                    }
                }else {
                    Toast.makeText(ManageUsers.this,"Something went wrong. Try again",Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ConnectionResponse> call, Throwable t) {
                Toast.makeText(ManageUsers.this,"Something went wrong. Try again",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_manage_user,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId()==R.id.addUser){
                openAddUserDialog();
        }
        return false;
    }
}