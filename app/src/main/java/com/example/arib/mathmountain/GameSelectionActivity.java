package com.example.arib.mathmountain;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class GameSelectionActivity extends AppCompatActivity {

    MediaPlayer song;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        song = MediaPlayer.create(this, R.raw.song);
        song.setLooping(true);
        song.start();
        setContentView(R.layout.activity_game_selection);
    }

    @Override
    public void onStop() {
        super.onStop();
        song.stop();
    }

    @Override
    public void onStart() {
        super.onStart();
        song.start();
    }

    public void speedClimb(View view) {
        song.stop();
        startActivity(new Intent(this, MainActivity.class));
    }

    public void countdown(View view) {
        song.stop();
        startActivity(new Intent(this, CountdownActivity.class));
    }

    public void continuous(View view) {
        song.stop();
        startActivity(new Intent(this, ContinuousActivity.class));
    }
}
