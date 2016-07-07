package com.example.arib.mathmountain;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLClientInfoException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arib on 6/18/2016.
 */
public class ContinuousDatabase extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION   = 2;
    private static final String DATABASE_NAME   = "highScoresContinuous";
    private static final String TABLE_TEAMS     = "highscoresContinuous";

    // Contacts Table column headers

    private static final String KEY_ID          = "id";
    private static final String KEY_HIGHSCORE   = "number";

    //default constructor
    public ContinuousDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TEAMS_TABLE = "CREATE TABLE " + TABLE_TEAMS + "("
                + KEY_HIGHSCORE + " TEXT" + ")";
        db.execSQL(CREATE_TEAMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEAMS);

        // Create Tables again
        onCreate(db);
    }

    public void addHighScore(String score) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_HIGHSCORE, score);

        db.insert(TABLE_TEAMS, null, values);
        db.close();
    }

    public int getHighScore(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(cursor != null) {
            cursor.moveToFirst();
        }

        return 0;
    }

    public List<String> getAllTeams() {
        List<String> scores = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TEAMS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String score = cursor.getString(0);
                scores.add(score + "");
            } while (cursor.moveToNext());
        }

        // return contact list
        return scores;

    }


    public void deleteScores() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEAMS);
    }
}
