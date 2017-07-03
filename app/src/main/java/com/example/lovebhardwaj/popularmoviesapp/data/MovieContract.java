package com.example.lovebhardwaj.popularmoviesapp.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract to facilitate database and provider
 */

public final class MovieContract {

    static final String CONTENT_AUTHORITY = "com.example.lovebhardwaj.popularmoviesapp.Data";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    static final String PATH_MOVIES = "FavoriteMovies";

    private MovieContract(){}//Private constructor

    public static final class MovieEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "FavoriteMovies";

        public static final String MOVIE_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String MOVIE_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        public static final String MOVIE_ID = "movieId";
        public static final String MOVIE_TITLE = "title";
        public static final String MOVIE_POSTER_PATH = "poster";
        public static final String MOVIE_PLOT = "plot";
        public static final String MOVIE_RATING = "rating";
        public static final String MOVIE_RELEASE_DATE = "release";

        private MovieEntry(){} //Private constructor

    }

    public static long getRowId(Uri uri){
        //Utility method get the id of the row for which the uri was passed
        return ContentUris.parseId(uri);
    }
}
