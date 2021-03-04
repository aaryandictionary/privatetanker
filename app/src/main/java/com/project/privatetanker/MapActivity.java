package com.project.privatetanker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.project.privatetanker.Database.Database;
import com.project.privatetanker.Database.UserInterface;
import com.project.privatetanker.Models.Address.AddAddressModel;
import com.project.privatetanker.Models.Address.AddAddressResponse;
import com.project.privatetanker.Models.Address.AddressData;
import com.project.privatetanker.Models.Address.CheckLocationModel;
import com.project.privatetanker.Models.Address.CheckLocationResponse;
import com.project.privatetanker.Utils.DataProcessor;
import com.skyfishjy.library.RippleBackground;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;

    private AutocompleteSupportFragment places;
    RelativeLayout relDialog;

    private Location mLastKnownLocation;
    private LocationCallback locationCallback;
    private View mapView;
    private Button btnSelectLocation;
    private TextInputLayout txtAddress, txtAddressType;

    private UserInterface userInterface;

    private final float DEFAULT_ZOOM = (float) 17.0;
    private Integer selected_area_id = -1;
    private RippleBackground ripple_marker_bg;

    DataProcessor dataProcessor;
    LinearLayout linSearch;

    Geocoder geocoder;
    List<Address> addresses;

    FirebaseAuth mAuth;

    String openType, place;

    ImageButton imgBtnMyLocation;

    private int id = -1,position;
    private double lat=0,lng=0;

    private int RETRY_CHECK = 1;
    private int RETRY_ADD = 1;
    private int RETRY_UPDATE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        btnSelectLocation = findViewById(R.id.btnSelectLocation);
        relDialog = findViewById(R.id.relDialog);
        ripple_marker_bg = findViewById(R.id.ripple_marker_bg);
        txtAddress = findViewById(R.id.txtAddress);
        txtAddressType = findViewById(R.id.txtAddressType);
        imgBtnMyLocation = findViewById(R.id.imgBtnMyLocation);
        linSearch = findViewById(R.id.linSearch);

        userInterface = Database.getClient(this).create(UserInterface.class);

        if (getIntent().hasExtra("id")) {
            id = getIntent().getIntExtra("id", -1);
            position=getIntent().getIntExtra("position",-1);
            txtAddress.getEditText().setText(getIntent().getStringExtra("address"));
            txtAddressType.getEditText().setText(getIntent().getStringExtra("address_type"));
            lat=getIntent().getDoubleExtra("lat",0);
            lng=getIntent().getDoubleExtra("lng",0);
        }

        openType = getIntent().getStringExtra("openType");

        geocoder = new Geocoder(this, Locale.getDefault());

        dataProcessor = DataProcessor.getInstance(this);

        mAuth = FirebaseAuth.getInstance();

        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapActivity.this);
        Places.initialize(MapActivity.this, "AIzaSyDG5pE1izylOdzxdRvEsyJr9K9rJsRDBY4");
        placesClient = Places.createClient(this);
        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();


        btnSelectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(txtAddress.getEditText().getText().toString()) && !TextUtils.isEmpty(txtAddressType.getEditText().getText().toString())) {
                    checkLocation(id);
                } else {
                    txtAddress.setError("Enter address");
                    txtAddressType.setError("Enter address type");
                }

            }
        });


        places = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.places_search_fragment);
        places.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME));

        places.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), DEFAULT_ZOOM));
            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(MapActivity.this, "Failed to find the place", Toast.LENGTH_SHORT).show();
            }
        });

        imgBtnMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLocationPermission();
                if (mMap != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length != 0) {
                getDeviceLocation();
            }
        }
    }

    /* @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        showMyLocationButton();
    }*/

    /*private void showMyLocationButton(){
        if (mapView!=null && mapView.findViewById(Integer.parseInt("1"))!=null&&mMap!=null){
            View locationButton =((View)mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams=(RelativeLayout.LayoutParams)locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layoutParams.setMargins(0,0,40,relDialog.getHeight()+100);
            //Toast.makeText(MapActivity.this,""+relDialog.getHeight()+" mea "+relDialog.getMeasuredHeight(),Toast.LENGTH_SHORT).show();
        }
    }*/


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                getAddressData();
            }
        });


        //showMyLocationButton();

        //Check if GPS is enabled or not and then request user to enable

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(50000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(MapActivity.this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                if (lat!=0&&lng!=0){
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), DEFAULT_ZOOM));
                    lat=0;
                    lng=0;
                }else {
                    checkLocationPermission();
                }
//                checkLocationPermission();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(MapActivity.this, 51);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        // showMyLocationButton();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (resultCode == RESULT_OK) {
                checkLocationPermission();
            }
        }
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(MapActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_LOCATION_PERMISSION);
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getDeviceLocation();
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
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_permission_required, null);
        alertDialog.setView(view);

        RelativeLayout relRequestPermission = view.findViewById(R.id.relRequestPermission);
        relRequestPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                checkLocationPermission();
            }
        });

        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    mLastKnownLocation = task.getResult();
                    if (mLastKnownLocation != null) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                    } else {
                        final LocationRequest locationRequest = LocationRequest.create();
                        locationRequest.setInterval(10000);
                        locationRequest.setFastestInterval(5000);
                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                super.onLocationResult(locationResult);
                                if (locationRequest == null) {
                                    return;
                                } else {
                                    mLastKnownLocation = locationResult.getLastLocation();
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                    mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                }
                            }
                        };
                        mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    }
                } else {
                    Toast.makeText(MapActivity.this, "Unable to get last Location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getAddressData() {
        final LatLng currentMarkerLocation = mMap.getCameraPosition().target;
        getAddress(currentMarkerLocation.latitude, currentMarkerLocation.longitude);
    }

    private void getAddress(final double lat, final double lng) {
        relDialog.setEnabled(false);
        linSearch.setEnabled(false);
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses.size() != 0)
                txtAddress.getEditText().setText(addresses.get(0).getAddressLine(0));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void checkLocation(Integer id) {
        ripple_marker_bg.startRippleAnimation();
        mapView.setEnabled(false);
        txtAddress.setEnabled(false);
        txtAddressType.setEnabled(false);
        final LatLng currentMarkerLocation = mMap.getCameraPosition().target;
        CheckLocationModel checkLocationModel = new CheckLocationModel();
        checkLocationModel.setLat(currentMarkerLocation.latitude);
        checkLocationModel.setLng(currentMarkerLocation.longitude);
        RETRY_CHECK=0;

        userInterface.checkServiceable(checkLocationModel).enqueue(new Callback<CheckLocationResponse>() {
            @Override
            public void onResponse(Call<CheckLocationResponse> call, Response<CheckLocationResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus().equals("SUCCESS")) {
                        if (response.body().getData() != null) {
                            selected_area_id = Integer.parseInt(response.body().getData().getId());
                            if (id == -1) {
                                addNewAddress();
                            } else {
                                updateAddress();
                            }
                        } else {
                            selected_area_id = -1;
                            Intent intent = new Intent(MapActivity.this, UnserviceableLocation.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                } else {
                    Toast.makeText(MapActivity.this, "Something went wrong. Try again" + response.code() + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CheckLocationResponse> call, Throwable t) {
                if (RETRY_CHECK < 3) {
                    RETRY_CHECK += 1;
                    call.clone().enqueue(this);
                } else {
                    ripple_marker_bg.stopRippleAnimation();
                    mapView.setEnabled(true);
                    txtAddress.setEnabled(true);
                    txtAddressType.setEnabled(true);
                    Toast.makeText(MapActivity.this, "Something went wrong. Try again Fail " + t.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void addNewAddress() {
        final LatLng currentMarkerLocation = mMap.getCameraPosition().target;
        AddAddressModel addAddressModel = new AddAddressModel();
        addAddressModel.setLat(currentMarkerLocation.latitude);
        addAddressModel.setLng(currentMarkerLocation.longitude);
        addAddressModel.setUser_id(dataProcessor.getInteger(DataProcessor.USER_ID));
        addAddressModel.setAddress(txtAddress.getEditText().getText().toString());
        addAddressModel.setAddress_type(txtAddressType.getEditText().getText().toString());

        Toast.makeText(MapActivity.this, "Add called", Toast.LENGTH_SHORT).show();

        userInterface.addAddress(addAddressModel).enqueue(new Callback<AddAddressResponse>() {
            @Override
            public void onResponse(Call<AddAddressResponse> call, Response<AddAddressResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus().equals("SUCCESS")) {
                        dataProcessor.saveInteger(DataProcessor.SELECTED_ADDRESS_ID, response.body().getData().getId());
                        Toast.makeText(MapActivity.this, "New Address added successfully", Toast.LENGTH_SHORT).show();
                        //                    onBackPressed();
                        setData(response.body().getData());
                    }
                } else {
                    mapView.setEnabled(true);
                    txtAddress.setEnabled(true);
                    txtAddressType.setEnabled(true);
                    Toast.makeText(MapActivity.this, "Something went wrong. Try again" + response.code() + response.message(), Toast.LENGTH_SHORT).show();
                }
                ripple_marker_bg.stopRippleAnimation();
            }

            @Override
            public void onFailure(Call<AddAddressResponse> call, Throwable t) {
                if (RETRY_ADD < 3) {
                    RETRY_ADD += 1;
                    call.clone().enqueue(this);
                } else {
                    ripple_marker_bg.stopRippleAnimation();
                    mapView.setEnabled(true);
                    txtAddress.setEnabled(true);
                    txtAddressType.setEnabled(true);
                    Toast.makeText(MapActivity.this, "Something went wrong. Try again later" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateAddress() {
        if (id != -1) {
            final LatLng currentMarkerLocation = mMap.getCameraPosition().target;
            AddAddressModel addAddressModel = new AddAddressModel();
            addAddressModel.setLat(currentMarkerLocation.latitude);
            addAddressModel.setLng(currentMarkerLocation.longitude);
            addAddressModel.setUser_id(dataProcessor.getInteger(DataProcessor.USER_ID));
            addAddressModel.setAddress(txtAddress.getEditText().getText().toString());
            addAddressModel.setAddress_type(txtAddressType.getEditText().getText().toString());
            addAddressModel.setId(id);

            userInterface.editAddress(addAddressModel).enqueue(new Callback<AddAddressResponse>() {
                @Override
                public void onResponse(Call<AddAddressResponse> call, Response<AddAddressResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null && response.body().getStatus().equals("SUCCESS")) {
                            dataProcessor.saveInteger(DataProcessor.SELECTED_ADDRESS_ID, response.body().getData().getId());
                            Toast.makeText(MapActivity.this, "Address modified successfully", Toast.LENGTH_SHORT).show();
                            //                        onBackPressed();
                            setData(response.body().getData());
                        }
                    } else {
                        mapView.setEnabled(true);
                        txtAddress.setEnabled(true);
                        txtAddressType.setEnabled(true);
                        Toast.makeText(MapActivity.this, "Something went wrong. Try again", Toast.LENGTH_SHORT).show();
                    }
                    ripple_marker_bg.stopRippleAnimation();
                }

                @Override
                public void onFailure(Call<AddAddressResponse> call, Throwable t) {
                    if (RETRY_UPDATE < 3) {
                        RETRY_UPDATE += 1;
                        call.clone().enqueue(this);
                    } else {
                        ripple_marker_bg.stopRippleAnimation();
                        mapView.setEnabled(true);
                        txtAddress.setEnabled(true);
                        txtAddressType.setEnabled(true);
                        Toast.makeText(MapActivity.this, "Something went wrong. Try again later" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void setData(AddressData addressData){
        Intent intent=new Intent();
        Bundle bundle=new Bundle();
        bundle.putInt("id",addressData.getId());
        bundle.putInt("user_id",addressData.getUserId());
        bundle.putString("address",addressData.getAddress());
        bundle.putString("address_type",addressData.getAddressType());
        bundle.putString("created_at",addressData.getCreatedAt());
        bundle.putString("updated_at",addressData.getUpdatedAt());
        bundle.putDouble("lat",addressData.getLat());
        bundle.putDouble("lng",addressData.getLng());

        if (id!=-1){
            bundle.putInt("position",position);
            intent.putExtra("edit_address",bundle);
            setResult(RESULT_OK,intent);
            finish();
        }else {
            intent.putExtra("add_address",bundle);
            setResult(RESULT_OK,intent);
            finish();
        }
    }


}
