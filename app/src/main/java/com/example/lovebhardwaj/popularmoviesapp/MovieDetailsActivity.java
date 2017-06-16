package com.example.lovebhardwaj.popularmoviesapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lovebhardwaj.popularmoviesapp.Utilities.JsonDataUtility;
import com.example.lovebhardwaj.popularmoviesapp.Utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

public class MovieDetailsActivity extends AppCompatActivity {
    private JsonDataUtility.MovieItem mMovieItem;//Movie item to be used to fetch data

    //View to fill the ui with data
    private TextView movieTitleTextView;
    private TextView releaseDateTextView;
    private TextView ratingTextView;
    private TextView plotTextView;
    private ImageView posterImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        if (this.getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);//Enable the up button
        }
        //Fetch the movie that was clicked
        mMovieItem = (JsonDataUtility.MovieItem) getIntent().getSerializableExtra("MovieItem");

        movieTitleTextView = (TextView) findViewById(R.id.movieTitleTextView);
        releaseDateTextView = (TextView) findViewById(R.id.releaseDateTextView);
        ratingTextView = (TextView) findViewById(R.id.ratingTextView);
        plotTextView = (TextView) findViewById(R.id.plotTextView);
        posterImageView = (ImageView) findViewById(R.id.posterImageView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mMovieItem != null) {
            movieTitleTextView.setText(mMovieItem.getTitle());
            Picasso.with(this).load(NetworkUtils.buildImageUrl(mMovieItem.getPosterPath())).into(posterImageView);
            releaseDateTextView.append(mMovieItem.getReleaseDate());
            ratingTextView.append(mMovieItem.getUserRating());
            plotTextView.append(mMovieItem.getPlotSynopsis());
        }
    }
}
