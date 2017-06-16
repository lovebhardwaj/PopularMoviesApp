package com.example.lovebhardwaj.popularmoviesapp.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class t be used to process the json data we get from the api
 */

public class JsonDataUtility {
    private static final String RESULT_ARRAY = "results";
    private static final String TITLE = "title";
    private static final String POSTER_PATH = "poster_path";
    private static final String PLOT_SYNOPSIS = "overview";
    private static final String USER_RATING = "vote_average";
    private static final String RELEASE_DATE = "release_date";

    public static ArrayList<MovieItem> moviesData(JSONObject apiJsonObject){

        ArrayList<MovieItem> mMovieItems;
        mMovieItems = new ArrayList<>();
        try {
            JSONArray resultArray = apiJsonObject.getJSONArray(RESULT_ARRAY);
            for (int i=0; i < resultArray.length(); i++){
                JSONObject movieObject = resultArray.getJSONObject(i);
                String title = movieObject.getString(TITLE);
                String posterPath = movieObject.getString(POSTER_PATH);
                String plotSynopsis = movieObject.getString(PLOT_SYNOPSIS);
                String userRating = movieObject.getString(USER_RATING);
                String releaseDate = movieObject.getString(RELEASE_DATE);

                MovieItem movieItem = new MovieItem(title, posterPath, plotSynopsis, userRating, releaseDate);
                mMovieItems.add(movieItem);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        return mMovieItems;
    }


    public static class MovieItem implements Serializable{
        private final String title;
        private final String posterPath;
        private final String plotSynopsis;
        private final String userRating;
        private final String releaseDate;
        private static final long serialVersionUID = 6132017L;

        MovieItem(String title, String posterPath,
                         String plotSynopsis, String userRating, String releaseDate){
            this.title = title;
            this.posterPath = posterPath;
            this.plotSynopsis = plotSynopsis;
            this.userRating = userRating;
            this.releaseDate = releaseDate;
        }

        public String getTitle() {
            return title;
        }

        public String getPosterPath() {
            return posterPath;
        }

        public String getPlotSynopsis() {
            return plotSynopsis;
        }

        public String getUserRating() {
            return userRating;
        }

        public String getReleaseDate() {
            return releaseDate;
        }
    }
}
