package com.example.lovebhardwaj.popularmoviesapp.Utilities;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by love on 6/12/2017.
 */

public class NetworkUtils {
    private static final String TAG = "NetworkUtils";
    //Class to deal with all the network related tasks

    //Base url to fetch the movie data
    private final static String BASE_THE_MOVIE_DB_URL = "http://api.themoviedb.org/3/movie";
    private final static String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w500/";


    //API key to required to fetch the data from the api
    private final static String API_KEY = "api_key";
    private final static String API_KEY_VALUE = "5779e2ac11fe018bdc8a95ba1a89a44a";


    public static URL buildDataUrl(String selectionCriteria){
        Uri builtUri = Uri.parse(BASE_THE_MOVIE_DB_URL).buildUpon()
                        .appendPath(selectionCriteria)
                        .appendQueryParameter(API_KEY, API_KEY_VALUE)
                        .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }

    public static Uri buildImageUrl(String posterPath){
        Log.d(TAG, "buildImageUrl: poster path : " + posterPath);

        Uri builtUri = Uri.parse(BASE_IMAGE_URL + posterPath);

        Log.d(TAG, "buildImageUrl: built uri : " + builtUri);


        return builtUri;
    }

    public static class VolleyUtility{
        private final Context mContext;
        private RequestQueue mRequestQue;
        private static VolleyUtility mVolleyInstance;

        private VolleyUtility(Context context){
            mContext = context;
        }

        public static synchronized VolleyUtility getInstance(Context context){
            if (mVolleyInstance == null){
                mVolleyInstance = new VolleyUtility(context);
            }
            return mVolleyInstance;
        }

        public RequestQueue getRequestQue(){
            if (mRequestQue == null){
                mRequestQue = Volley.newRequestQueue(mContext.getApplicationContext());
            }
            return mRequestQue;
        }

        public void addToRequestQue(Request request){
            getRequestQue().add(request);
        }


    }

}
