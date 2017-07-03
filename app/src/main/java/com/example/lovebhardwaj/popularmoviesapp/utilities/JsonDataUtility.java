package com.example.lovebhardwaj.popularmoviesapp.utilities;

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

    private static final String MOVIE_ID = "id";
    private static final String TITLE = "title";
    private static final String POSTER_PATH = "poster_path";
    private static final String PLOT_SYNOPSIS = "overview";
    private static final String USER_RATING = "vote_average";
    private static final String RELEASE_DATE = "release_date";

    private static final String TRAILER_VIDEOS = "videos";
    private static final String RESULTS = "results";
    private static final String TRAILER_KEY = "key";
    private static final String TRAILER_NAME = "name";

    private static final String REVIEW = "reviews";
    private static final String REVIEW_AUTHOR = "author";
    private static final String REVIEW_CONTENT = "content";
    private static final String REVIEW_URL = "url";

    public static ArrayList<MovieItem> moviesData(JSONObject apiJsonObject){

        ArrayList<MovieItem> mMovieItems;
        mMovieItems = new ArrayList<>();
        try {
            JSONArray resultArray = apiJsonObject.getJSONArray(RESULT_ARRAY);
            for (int i=0; i < resultArray.length(); i++){
                JSONObject movieObject = resultArray.getJSONObject(i);
                String movieId = movieObject.getString(MOVIE_ID);
                String title = movieObject.getString(TITLE);
                String posterPath = movieObject.getString(POSTER_PATH);
                String plotSynopsis = movieObject.getString(PLOT_SYNOPSIS);
                String userRating = movieObject.getString(USER_RATING);
                String releaseDate = movieObject.getString(RELEASE_DATE);

                MovieItem movieItem = new MovieItem(movieId, title, posterPath, plotSynopsis, userRating, releaseDate);
                mMovieItems.add(movieItem);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        return mMovieItems;
    }

    //Method to get the trailer data parsed from the json object we get from the api
    public static ArrayList<TrailerInfo> trailerData(JSONObject apiObject){
        ArrayList<TrailerInfo> trailerList = new ArrayList<>();
        try{
            JSONObject videoObject = apiObject.getJSONObject(TRAILER_VIDEOS);
            JSONArray trailerArray = videoObject.getJSONArray(RESULTS);
            for (int i = 0; i < trailerArray.length(); i++){
                JSONObject trailerObject = trailerArray.getJSONObject(i);
                String key = trailerObject.getString(TRAILER_KEY);
                String name = trailerObject.getString(TRAILER_NAME);

                TrailerInfo trailerInfo = new TrailerInfo(key, name);
                trailerList.add(trailerInfo);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return trailerList;
    }

    //Method to get the reviews parsed from the json object get from the api
    public static ArrayList<ReviewInfo> reviewData(JSONObject apiObject){
        ArrayList<ReviewInfo> reviewsList = new ArrayList<>();
        try{
            JSONObject reviewObject = apiObject.getJSONObject(REVIEW);
            JSONArray reviewArray = reviewObject.getJSONArray(RESULTS);
            for (int i = 0; i < reviewArray.length(); i++){
                JSONObject review = reviewArray.getJSONObject(i);
                String author = review.getString(REVIEW_AUTHOR);
                String content = review.getString(REVIEW_CONTENT);
                String url = review.getString(REVIEW_URL);

                ReviewInfo reviewInfo = new ReviewInfo(author, content, url);
                reviewsList.add(reviewInfo);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        return reviewsList;
    }


    public static class MovieItem implements Serializable{
        private final String movieId;
        private final String title;
        private final String posterPath;
        private final String plotSynopsis;
        private final String userRating;
        private final String releaseDate;
        private static final long serialVersionUID = 6132017L;

        public MovieItem(String id, String title, String posterPath,
                         String plotSynopsis, String userRating, String releaseDate){
            this.movieId = id;
            this.title = title;
            this.posterPath = posterPath;
            this.plotSynopsis = plotSynopsis;
            this.userRating = userRating;
            this.releaseDate = releaseDate;
        }

        public String getMovieId() {
            return movieId;
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

    public static class TrailerInfo implements Serializable{
        private static final long serialVersionUID = 6162017L;
        private final String trailerKey;
        private final String trailerTitle;

        TrailerInfo(String trailerKey, String trailerTitle){
            this.trailerKey = trailerKey;
            this.trailerTitle = trailerTitle;
        }

        public String getTrailerKey() {
            return trailerKey;
        }

        public String getTrailerTitle() {
            return trailerTitle;
        }
    }

    public static class ReviewInfo implements Serializable{
        private static final long serialVersionUID = 61620171L;
        private final String author;
        private final String content;
        private final String url;

        ReviewInfo(String author, String content, String url){
            this.author = author;
            this.content = content;
            this.url = url;
        }

        public String getAuthor() {
            return author;
        }

        public String getContent() {
            return content;
        }

        public String getUrl() {
            //Not used in the code but can be used when needed
            return url;
        }
    }
}
