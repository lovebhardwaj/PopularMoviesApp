package com.example.lovebhardwaj.popularmoviesapp.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DbHelper class used by the content provider of the class to perform various database related functions
 */

public class MovieDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "MovieDbHelper";

    private static final String DATABASE_NAME = "favoritemovies.db";
    private static int DATABASE_VERSION = 1;

    private static MovieDbHelper instance = null;

    public static MovieDbHelper getInstance(Context context){
        if (instance == null){
            return new MovieDbHelper(context);
        }
        return instance;
    }

    private MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sqlQuery = "CREATE TABLE IF NOT EXISTS " + MovieContract.MovieEntry.TABLE_NAME + " (" + MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MovieContract.MovieEntry.MOVIE_ID + " TEXT NOT NULL UNIQUE, " + MovieContract.MovieEntry.MOVIE_TITLE
                + " TEXT NOT NULL, " + MovieContract.MovieEntry.MOVIE_POSTER_PATH + " TEXT NOT NULL, " + MovieContract.MovieEntry.MOVIE_PLOT + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.MOVIE_RATING + " TEXT NOT NULL, " + MovieContract.MovieEntry.MOVIE_RELEASE_DATE + " TEXT NOT NULL);";
        db.execSQL(sqlQuery);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Not implemented
    }
}
