package com.project.privatetanker.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class DataProcessor {

    public static final String USER_ID = "user_id";
    public static final String TOKEN = "token";
    public static final String NAME = "name";
    public static final String PHONE = "phone";
    public static final String CUSTOMER_TYPE = "customer_type";
    public static final String IMAGE = "image";
    public static final String STATUS = "status";
    public static final String FIREBASE_UID = "firebase_uid";
    public static final String IS_PASSCODE_SET = "is_password_set";
    public static final String IS_PASSCODE_SKIP = "is_password_skip";
    public static final String PASSCODE = "passcode";
    public static final String SELECTED_ADDRESS_ID = "selected_address_id";
    public static final String REQUIRED_PROFILE_COMPLETION = "required_profile_completion";
    public static final String SELECTED_AREA_ID = "selected_area_id";
    public static final String FCM_TOKEN = "fcm_token";

    public static final String PRESS_BACK_BOOK_TANKER = "press_back_book_tanker";
    public static final String FIRST_LOCATION_SAVED = "first_location_saved";


    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private static Context mCxt;

    private static DataProcessor INSTANCE=null;

    private DataProcessor(SharedPreferences preferences) {
        this.prefs = preferences;
        this.editor = preferences.edit();
    }

    public static synchronized DataProcessor getInstance(Context context){
        mCxt=context;
        if (INSTANCE==null){
            INSTANCE=new DataProcessor(context.getSharedPreferences("private_tanker", Context.MODE_PRIVATE));
        }

        return INSTANCE;
    }

    public void deleteAll(){
        editor.clear();
        editor.apply();
    }

    public  void saveString(String key, String value) {
        editor.putString(key,value);
        editor.apply();
    }

    public  void saveLong(String key, Long value) {
        editor.putLong(key,value);
        editor.apply();
    }

    public  void saveInteger(String key, int value) {
        editor.putInt(key,value);
        editor.apply();
    }

    public String getString(String key) {
        return prefs.getString(key,"");
    }
    public  int getInteger(String key) {
        return prefs.getInt(key,-1);
    }

    public void saveBoolean(String key, boolean value){
    editor.putBoolean(key,value);
    editor.apply();
    }

    public boolean getBoolean(String key){
        return prefs.getBoolean(key,false);
    }
    public long getLong(String key){
        return prefs.getLong(key,0);
    }

    public boolean isConnected(){
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)mCxt.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        } else
            connected = false;
        return connected;
    }



}
