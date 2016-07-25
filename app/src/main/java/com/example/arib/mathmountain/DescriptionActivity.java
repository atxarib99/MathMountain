package com.example.arib.mathmountain;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class DescriptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#102759")));
        } else {
            Log.d("ActionBar", "Error ActionBar was null");
        }
        setContentView(R.layout.activity_description);
    }

    public void seeMoreSpeed(View view) {
        TextView description = (TextView) findViewById(R.id.speedclimbdes);
        if (description != null) {
            description.setMaxLines(Integer.MAX_VALUE);
        }

    }

    public void seeMoreAval(View view) {
        TextView description = (TextView) findViewById(R.id.avalanchedes);
        if (description != null) {
            description.setMaxLines(Integer.MAX_VALUE);
        }

    }

    public void seeMoreEver(View view) {
        TextView description = (TextView) findViewById(R.id.everestdes);
        if (description != null) {
            description.setMaxLines(Integer.MAX_VALUE);
        }

    }




}