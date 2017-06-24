package com.example.lovebhardwaj.popularmoviesapp;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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

    //View to fill the ui with data
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

    private boolean isFavorite;

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
            Picasso.with(this).load(NetworkUtils.buildImageUrl(mMovieItem.getPosterPath())).into(posterImageView);
            releaseDateTextView.setText(mMovieItem.getReleaseDate());
            ratingTextView.setText(mMovieItem.getUserRating());
            plotTextView.setText(mMovieItem.getPlotSynopsis());
            String movieDetailUrl = NetworkUtils.buildVideoReviewUrl(mMovieItem.getMovieId()).toString();
            loadTrailerReviewData(movieDetailUrl);

        }

    }

    private void loadTrailerReviewData(String trailerReviewUrl){

        JsonObjectRequest trailerReviewObjectRequest = new JsonObjectRequest(Request.Method.GET, trailerReviewUrl, null, new Response.Listener<JSONObject>() {
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
}
