package com.example.arib.mathmountain;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class GameSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_selection);
    }

    public void speedClimb(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void countdown(View view) {
        startActivity(new Intent(this, CountdownActivity.class));
    }
    public void continuous(View view) {

    }
}
