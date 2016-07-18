package com.example.arib.mathmountain;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItemView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class GameSelectionActivity extends AppCompatActivity {

    MediaPlayer song;
    protected static boolean MUTED;
    Drawable mutedIcon;
    Drawable unmutedIcon;
    Thread mediaThread;
    Thread prefThread;
    Runnable saveMute = new Runnable() {
        @Override
        public void run() {
            SharedPreferences pref = getSharedPreferences("mute", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("muted", 1);
            editor.commit();
        }
    };
    Runnable saveUnmute = new Runnable() {
        @Override
        public void run() {
            SharedPreferences pref = getSharedPreferences("mute", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("muted", 0);
            editor.commit();
        }
    };
    Runnable startMedia = new Runnable() {
        @Override
        public void run() {
            song = MediaPlayer.create(GameSelectionActivity.this, R.raw.song);
            song.setLooping(true);
            song.start();
        }
    };
    Runnable stopMedia = new Runnable() {
        @Override
        public void run() {
            if(song.isPlaying())
                song.stop();
        }
    };
    Runnable toggle = new Runnable() {
        @Override
        public void run() {
            ActionMenuItemView menuItem = (ActionMenuItemView) findViewById(R.id.action_mute);
            if(MUTED) {
                if (menuItem != null) {
                    menuItem.setIcon(mutedIcon);
                }
            } else {
                assert menuItem != null;
                menuItem.setIcon(unmutedIcon);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = getSharedPreferences("mute", Context.MODE_PRIVATE);
        unmutedIcon = getDrawable(R.drawable.unmuteicon);
        mutedIcon = getDrawable(R.drawable.muteicon);
        int mode = pref.getInt("muted", 2);
        Log.d("mode", ""+mode);
        if(mode == 2 || mode == 0) {
            MUTED = false;
        } else {
            MUTED = true;
        }
        song = MediaPlayer.create(this, R.raw.song);
        song.setLooping(true);
        if(MUTED == false) {
            song.start();
        } else {
            song.stop();
        }
        setContentView(R.layout.activity_game_selection);
    }

    //creates the option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gameselction, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //handles what to do when a item is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // retrieves the id
        int id = item.getItemId();

        //when the get icon is pressed
        if(id == R.id.action_mute) {
            if(MUTED == true) {
                MUTED = false;
                mediaThread = new Thread(startMedia);
                mediaThread.start();
                Toast.makeText(this, "Game unmuted", Toast.LENGTH_LONG).show();
                prefThread = new Thread(saveUnmute);
                prefThread.start();
                this.runOnUiThread(toggle);
            } else {
                MUTED = true;
                mediaThread = new Thread(stopMedia);
                mediaThread.start();
                Toast.makeText(this, "Game muted", Toast.LENGTH_LONG).show();
                prefThread = new Thread(saveMute);
                prefThread.start();
                this.runOnUiThread(toggle);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
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
