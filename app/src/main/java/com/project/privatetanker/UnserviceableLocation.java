package com.project.privatetanker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

public class UnserviceableLocation extends AppCompatActivity {

    private RelativeLayout relSelectLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unserviceable_location);
        relSelectLocation=findViewById(R.id.relSelectLocation);

        relSelectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UnserviceableLocation.this,MapActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}