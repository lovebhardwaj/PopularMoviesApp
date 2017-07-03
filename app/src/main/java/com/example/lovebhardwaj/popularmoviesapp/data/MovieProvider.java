package com.example.lovebhardwaj.popularmoviesapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.android.volley.VolleyLog.TAG;

/**
 * Content provider for popular movies app
 */

public class MovieProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mDbHelper;


    private static final int MOVIE = 100;
    private static final int MOVIE_ID = 101;

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES, MOVIE);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES + "/#", MOVIE_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        //Return an instance of movie db helper
        mDbHelper = MovieDbHelper.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final int matcher = sUriMatcher.match(uri);
        //Execute the query on the movie database using the movieDb instance
        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        switch (matcher) {
            case MOVIE:
                //Query the whole table
                sqLiteQueryBuilder.setTables(MovieContract.MovieEntry.TABLE_NAME);
                break;
            case MOVIE_ID:
                sqLiteQueryBuilder.setTables(MovieContract.MovieEntry.TABLE_NAME);
                sqLiteQueryBuilder.appendWhere(MovieContract.MovieEntry.MOVIE_ID + "=" + MovieContract.getRowId(uri));
                break;
            default:
                throw new IllegalArgumentException("Called with a wrong Uri : " + uri);
        }
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor returnCursor = sqLiteQueryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int matcher = sUriMatcher.match(uri);

        switch (matcher) {
            case MOVIE:
                return MovieContract.MovieEntry.MOVIE_LIST_TYPE;
            case MOVIE_ID:
                return MovieContract.MovieEntry.MOVIE_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Called with a wrong Uri : " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int matcher = sUriMatcher.match(uri);
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Uri returnUri;
        long id;

        switch (matcher) {
            case MOVIE:

                id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, id);
                } else {
                    throw new SQLException("Unable to insert data");
                }
                break;
            default:
                throw new IllegalArgumentException("Called with a wrong Uri : " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int matcher = sUriMatcher.match(uri);
        SQLiteDatabase db;
        int count;
        switch (matcher) {
            case MOVIE:
                //Delete the whole table
                db = mDbHelper.getWritableDatabase();
                count = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_ID:
                db = mDbHelper.getWritableDatabase();
                selection = MovieContract.MovieEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(MovieContract.getRowId(uri))};
                count = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Called with a invalid Uri : " + uri);
        }
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        } else {
            throw new SQLException("Unable to delete the row with uri : " + uri);
        }
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int matcher = sUriMatcher.match(uri);
        SQLiteDatabase db;
        int count;
        switch (matcher) {
            case MOVIE:
                //Delete the whole table
                db = mDbHelper.getWritableDatabase();
                count = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case MOVIE_ID:
                db = mDbHelper.getWritableDatabase();
                selection = MovieContract.MovieEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(MovieContract.getRowId(uri))};
                count = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Called with a invalid Uri : " + uri);
        }
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        } else {
            Log.d(TAG, "update: nothing has changed");
        }
        return count;
    }
}
