package com.example.lovebhardwaj.popularmoviesapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.lovebhardwaj.popularmoviesapp.Adapters.ReviewListAdapter;
import com.example.lovebhardwaj.popularmoviesapp.Adapters.TrailerListAdapter;
import com.example.lovebhardwaj.popularmoviesapp.Data.MovieContract;
import com.example.lovebhardwaj.popularmoviesapp.Utilities.JsonDataUtility;
import com.example.lovebhardwaj.popularmoviesapp.Utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailsActivity extends AppCompatActivity {
    private static final String TAG = "MovieDetailsActivity";

    private JsonDataUtility.MovieItem mMovieItem;//Movie item to be used to fetch data

    private ArrayList<JsonDataUtility.TrailerInfo> mTrailerList;
    private ArrayList<JsonDataUtility.ReviewInfo> mReviewList;

    //Binding views
    @BindView(R.id.movieTitleTextView)
    TextView movieTitleTextView;
    @BindView(R.id.releaseDateTextView)
    TextView releaseDateTextView;
    @BindView(R.id.ratingTextView)
    TextView ratingTextView;
    @BindView(R.id.plotTextView)
    TextView plotTextView;
    @BindView(R.id.posterImageView)
    ImageView posterImageView;
    @BindView(R.id.trailersRecyclerView)
    RecyclerView trailerRecyclerView;
    @BindView(R.id.reviewRecyclerView)
    RecyclerView reviewRecyclerView;
    @BindView(R.id.favoriteButton)
    ToggleButton favoriteButton;
    @BindView(R.id.shareButton) ImageButton shareButton;
    @BindView(R.id.reviewHeadingTv) TextView reviewHeadingTv;
    @BindView(R.id.trailerHeadingTv) TextView trailerHeadingTv;

    private TrailerListAdapter trailerListAdapter;
    private ReviewListAdapter reviewListAdapter;

    private RecyclerView.LayoutManager trailerLayoutManager;
    private RecyclerView.LayoutManager reviewsLayoutManager;

    private boolean isFavorite;//Variable to store if the movie clicked is a favorite

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        if (this.getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);//Enable the up button
        }
        //Fetch the movie that was clicked
        mMovieItem = (JsonDataUtility.MovieItem) getIntent().getSerializableExtra(MainActivity.MOVIE_ITEM_KEY);
        isFavorite = getIntent().getBooleanExtra(MainActivity.FAVORITE, false);
        ButterKnife.bind(this);

        //Recycler view setup for respective recycler view
        trailerLayoutManager = new LinearLayoutManager(this);

        trailerListAdapter = new TrailerListAdapter(mTrailerList);
        trailerRecyclerView.setAdapter(trailerListAdapter);
        trailerRecyclerView.setLayoutManager(trailerLayoutManager);

        reviewsLayoutManager = new LinearLayoutManager(this);
        reviewListAdapter = new ReviewListAdapter(mReviewList);
        reviewRecyclerView.setAdapter(reviewListAdapter);
        reviewRecyclerView.setLayoutManager(reviewsLayoutManager);

        setUpToggleButton(favoriteButton);
        favoriteButton.setChecked(isFavorite);
        setUpOnClickListener(shareButton);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mMovieItem != null) {
            movieTitleTextView.setText(mMovieItem.getTitle());
            Picasso.with(this).load(NetworkUtils.buildDynamicUri(posterWidth(), mMovieItem.getPosterPath())).into(posterImageView);
            releaseDateTextView.setText(mMovieItem.getReleaseDate());
            ratingTextView.setText(mMovieItem.getUserRating());
            plotTextView.setText(mMovieItem.getPlotSynopsis());
            //Load the corresponding reviews and trailers, for the movie
            String movieDetailUrl = NetworkUtils.buildVideoReviewUrl(mMovieItem.getMovieId()).toString();
            loadTrailerReviewData(movieDetailUrl);
        }

    }

    private void loadTrailerReviewData(String trailerReviewUrl){
        //Using the volley class to load the data
        JsonObjectRequest trailerReviewObjectRequest = new JsonObjectRequest(Request.Method.GET,
                trailerReviewUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mTrailerList = JsonDataUtility.trailerData(response);
                if (!mTrailerList.isEmpty() && mTrailerList.size() > 0) {
                    trailerListAdapter.loadNewData(mTrailerList);
                }else {
                    trailerHeadingTv.append(getString(R.string.no_trailers));
                }
                mReviewList = JsonDataUtility.reviewData(response);
                if (!mReviewList.isEmpty() && mReviewList.size()>0) {
                    reviewListAdapter.loadNewData(mReviewList);
                }else {
                    reviewHeadingTv.append(getString(R.string.no_reviews));
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MovieDetailsActivity.this, R.string.error_response, Toast.LENGTH_SHORT).show();
            }
        });
        trailerReviewObjectRequest.setTag(TAG);
        NetworkUtils.VolleyUtility.getInstance(this).addToRequestQue(trailerReviewObjectRequest);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (NetworkUtils.VolleyUtility.getInstance(this).getRequestQue() != null) {
            NetworkUtils.VolleyUtility.getInstance(this).getRequestQue().cancelAll(TAG);
        }
    }

    private void setUpToggleButton(final ToggleButton toggleButton) {
        //Toggle button setup
        if (toggleButton != null) {
            toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if (isFavorite){
                            toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.star_golden));
                        }else {

                            toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.star_golden));
                            //Add to favorites

                            ContentValues values = new ContentValues();
                            values.put(MovieContract.MovieEntry.MOVIE_ID, mMovieItem.getMovieId());
                            values.put(MovieContract.MovieEntry.MOVIE_TITLE, mMovieItem.getTitle());
                            values.put(MovieContract.MovieEntry.MOVIE_POSTER_PATH, mMovieItem.getPosterPath());
                            values.put(MovieContract.MovieEntry.MOVIE_PLOT, mMovieItem.getPlotSynopsis());
                            values.put(MovieContract.MovieEntry.MOVIE_RATING, mMovieItem.getUserRating());
                            values.put(MovieContract.MovieEntry.MOVIE_RELEASE_DATE, mMovieItem.getReleaseDate());

                            long rowId = MovieContract.getRowId(getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values));

                            if (rowId > 0) {
                                Toast.makeText(MovieDetailsActivity.this, "Successfully added to the favorites list", Toast.LENGTH_SHORT).show();
                            }
                        }

                    } else {
                        toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.star_black));
                        //Remove from favorites
                        int rowsDeleted = getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, MovieContract.MovieEntry.MOVIE_ID + "=?",
                                new String[]{mMovieItem.getMovieId()});
                        if (rowsDeleted > 0) {
                            Toast.makeText(MovieDetailsActivity.this, "Deleted from the favorites list", Toast.LENGTH_SHORT).show();
                        }

                    }

                }
            });
        }
    }

    private void setUpOnClickListener(ImageButton shareButton){
        //Setting up the share button to share first trailer
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mTrailerList.isEmpty() && mTrailerList.size() > 0) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, NetworkUtils.youtubeUri(mTrailerList.get(0).getTrailerKey()).toString());
                    startActivity(Intent.createChooser(intent, "Share trailer using: "));
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);//Inflate the menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_sort_order:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String posterWidth(){
        //Method to check the screen size and display relevant poster size
        //Poster will cover around one third of the screen
        String returnString;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        if (screenWidth/92 < 3){
            returnString = "92";
        }else if (screenWidth/154 < 3){
            returnString = "154";
        }else if (screenWidth/185 < 3){
            returnString = "185";
        }else if (screenWidth/342 < 3){
            returnString = "342";
        }else if (screenWidth/500 < 3){
            returnString = "500";
        }else if (screenWidth/780 < 3){
            returnString = "780";
        }else {
            returnString = "500";
        }
        return returnString;
    }
}
