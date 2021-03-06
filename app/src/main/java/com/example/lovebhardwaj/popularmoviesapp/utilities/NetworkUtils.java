package com.example.lovebhardwaj.popularmoviesapp.utilities;

import android.content.Context;
import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Utility class to be used for network related jobs
 */

public class NetworkUtils {
    //Class to deal with all the network related tasks
    public static final String BASE_POSTER_WIDTH = "500";
    //Base url to fetch the movie data
    private final static String BASE_THE_MOVIE_DB_URL = "http://api.themoviedb.org/3/movie";
    private final static String BASE_YOUTUBE_URL = "https://www.youtube.com/watch";
    private final static String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w" + BASE_POSTER_WIDTH + "/";


    //API key to required to fetch the data from the api
    private final static String API_KEY = "api_key";
    private final static String API_KEY_VALUE = "";

    private final static String APPEND_TO_RESPONSE = "append_to_response";
    private final static String APPEND_TO_RESPONSE_VALUE = "videos,reviews";
    private final static String YOUTUBE_QUERY_PARAMETER = "v";

    public static URL buildDataUrl(String selectionCriteria) {
        Uri builtUri = Uri.parse(BASE_THE_MOVIE_DB_URL).buildUpon()
                .appendPath(selectionCriteria)
                .appendQueryParameter(API_KEY, API_KEY_VALUE)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static Uri buildImageUrl(String posterPath) {
        return Uri.parse(BASE_IMAGE_URL + posterPath);
    }

    public static Uri buildDynamicUri(String posterWidth, String posterPath){
        //Creating uri for downloading the poster based on the screen size
        return Uri.parse("http://image.tmdb.org/t/p/w" + posterWidth + "/" + posterPath);
    }

    public static URL buildVideoReviewUrl(String movieId) {
        URL videoReviewApiUrl = buildDataUrl(movieId);
        URL url = null;
        try {
            Uri videoReviewApiUri = Uri.parse(videoReviewApiUrl.toURI().toString()).buildUpon()
                    .appendQueryParameter(APPEND_TO_RESPONSE, APPEND_TO_RESPONSE_VALUE).build();
            url = new URL(videoReviewApiUri.toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;

    }

    public static Uri youtubeUri(String trailerKey) {
        return Uri.parse(BASE_YOUTUBE_URL).buildUpon()
                .appendQueryParameter(YOUTUBE_QUERY_PARAMETER, trailerKey)
                .build();

    }

    public static class VolleyUtility {
        private static Context mContext;
        private RequestQueue mRequestQue;
        private static VolleyUtility mVolleyInstance;

        private VolleyUtility(Context context) {
            mContext = context.getApplicationContext();
        }

        public static synchronized VolleyUtility getInstance(Context context) {
            if (mVolleyInstance == null) {
                mVolleyInstance = new VolleyUtility(context);
            }
            return mVolleyInstance;
        }

        public RequestQueue getRequestQue() {
            if (mRequestQue == null) {
                mRequestQue = Volley.newRequestQueue(mContext.getApplicationContext());
            }
            return mRequestQue;
        }

        public void addToRequestQue(Request request) {
            getRequestQue().add(request);
        }


    }

}
