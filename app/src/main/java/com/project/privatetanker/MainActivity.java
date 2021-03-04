package com.project.privatetanker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.Circle;
import com.google.android.material.navigation.NavigationView;
import com.project.privatetanker.Utils.DataProcessor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,DrawerLayout.DrawerListener {

    private static final int REQUEST_LOCATION_PERMISSION = 90;
    private RelativeLayout relBookTanker;
    private LinearLayout linMyBookings,linTransactions,linMyProfile,linManageUsers;

    private DataProcessor dataProcessor;

    private DrawerLayout drawerMain;
    private ActionBarDrawerToggle mActionBarDrawerToogle;

    private CircleImageView circularProfile;
    private TextView txtCustomerName,txtCustomerType,txtMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeView();
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dataProcessor=DataProcessor.getInstance(this);

        drawerMain=findViewById(R.id.drawerMain);
        setUpDrawer();
        drawerMain.addDrawerListener(this);

        mActionBarDrawerToogle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        NavigationView navigation=findViewById(R.id.navigation);
        navigation.setNavigationItemSelectedListener(this);

        View headerView=navigation.getHeaderView(0);
        circularProfile=headerView.findViewById(R.id.circularProfile);
        txtCustomerName=headerView.findViewById(R.id.txtCustomerName);
        txtCustomerType=headerView.findViewById(R.id.txtCustomerType);
        txtMobile=headerView.findViewById(R.id.txtMobile);

        setData();

        relBookTanker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLocationPermission();
            }
        });

        linMyBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkProfileCompletion()){
                    Intent intent = new Intent(MainActivity.this, BookingList.class);
                    startActivity(intent);
                }
            }
        });

        linTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkProfileCompletion()){
                    Intent intent = new Intent(MainActivity.this, BookingList.class);
                    startActivity(intent);
                }
            }
        });

        linMyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkProfileCompletion()){
                    Intent intent = new Intent(MainActivity.this, MyProfile.class);
                    startActivity(intent);
                }
            }
        });

        linManageUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkProfileCompletion()){
                    Intent intent = new Intent(MainActivity.this, ManageUsers.class);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean checkProfileCompletion(){
        if (dataProcessor.getBoolean(DataProcessor.REQUIRED_PROFILE_COMPLETION)){
            Intent intent=new Intent(MainActivity.this,EditProfile.class);
            intent.putExtra("type",0);
            startActivity(intent);
            return false;
        }
        return true;
    }

    private void setData(){
        txtCustomerName.setText(dataProcessor.getString(DataProcessor.NAME));
        txtMobile.setText("+91 "+dataProcessor.getString(DataProcessor.PHONE));
        txtCustomerType.setText(dataProcessor.getString(DataProcessor.CUSTOMER_TYPE));
        Glide.with(this).load(dataProcessor.getString(DataProcessor.IMAGE)).placeholder(R.drawable.ic_profile_placeholder).into(circularProfile);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_home,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActionBarDrawerToogle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToogle.onConfigurationChanged(newConfig);
    }

    private void setUpDrawer() {
        mActionBarDrawerToogle=new ActionBarDrawerToggle(this,drawerMain,R.string.open,R.string.close);

        mActionBarDrawerToogle.setDrawerIndicatorEnabled(true);
        drawerMain.setDrawerListener(mActionBarDrawerToogle);
    }

    private void initializeView(){
        relBookTanker=findViewById(R.id.relBookTanker);
        linMyBookings=findViewById(R.id.linMyBookings);
        linTransactions=findViewById(R.id.linTransactions);
        linMyProfile=findViewById(R.id.linMyProfile);
        linManageUsers=findViewById(R.id.linManageUsers);
    }

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {

    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        drawerMain.closeDrawer(Gravity.LEFT);

        switch (id){
            case R.id.navBookTanker:
                checkLocationPermission();
                break;
            case R.id.navBookingHistory:
            case R.id.navTransactions:
                if (checkProfileCompletion()){
                    Intent intent = new Intent(MainActivity.this, BookingList.class);
                    startActivity(intent);
                }
                break;
            case R.id.navManageUsers:
                if (checkProfileCompletion()){
                    Intent intent = new Intent(MainActivity.this, ManageUsers.class);
                    startActivity(intent);
                }
                break;
            case R.id.navMyProfile:
                if (checkProfileCompletion()){
                    Intent intent = new Intent(MainActivity.this, MyProfile.class);
                    startActivity(intent);
                }
                break;
            case R.id.navHelpSupport:
                String urlHelp = "https://api.whatsapp.com/send?phone=+919358174783";
                openLink(urlHelp);
                break;
            case R.id.navShare:
                String urlShare = "https://google.com";
                openLink(urlShare);
                break;
            case R.id.navRateUs:
                String urlRate = "https://google.com";
                openLink(urlRate);
                break;
            case R.id.navTermsConditions:
                String urlTerms = "https://google.com";
                openLink(urlTerms);
                break;
            case R.id.navPrivacyPolicy:
                String urlPrivacy = "https://google.com";
                openLink(urlPrivacy);
                break;
        }
        return false;
    }

    private void openLink(String link){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(browserIntent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mActionBarDrawerToogle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }


    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce){
            super.onBackPressed();
            this.finishAffinity();
        }else{
            this.doubleBackToExitPressedOnce=true;
            Toast.makeText(this,"Please click BACK again to exit",Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            },2000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0) {
                checkLocationPermission();
            }
        }
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_LOCATION_PERMISSION);
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            isLocationGranted = true;
            if (checkProfileCompletion()){
                Intent intent=new Intent(MainActivity.this,BookTanker.class);
                startActivity(intent);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                showPermissionDialog();
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                showPermissionDialog();
            } else {
                requestLocationPermission();
            }
        } else {
            requestLocationPermission();
        }
    }

    private void showPermissionDialog() {
        androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(this).create();
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_permission_required, null);
        alertDialog.setView(view);

        RelativeLayout relRequestPermission = view.findViewById(R.id.relRequestPermission);
        relRequestPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                requestLocationPermission();
            }
        });

        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
}