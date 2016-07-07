package com.example.arib.mathmountain;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class HighScoresActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.highscoresLayout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            relativeLayout.setBackground(getDrawable(R.drawable.mountainten));
        }
        ListView listView = (ListView) findViewById(R.id.scoresList);
        Intent intent = getIntent();
        int classFrom = intent.getIntExtra("class", 0);
        ArrayList<String> times;
        switch (classFrom) {
            case 1 : times = MainActivity.times; break;
            case 2 : times = CountdownActivity.times; break;
            case 3 : times = ContinuousActivity.times; break;
            default : times = MainActivity.times; break;
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.listview_item, times);
        listView.setAdapter(arrayAdapter);
    }
}
