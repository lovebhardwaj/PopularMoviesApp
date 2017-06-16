package com.example.lovebhardwaj.popularmoviesapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.lovebhardwaj.popularmoviesapp.Utilities.JsonDataUtility;
import com.example.lovebhardwaj.popularmoviesapp.Utilities.MovieAdapter;
import com.example.lovebhardwaj.popularmoviesapp.Utilities.NetworkUtils;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MovieAdapter.OnPosterClickListener {
    private static final String TAG = "MainActivity";
    //Selection criteria, based on which data will be fetched
    final static String SELECTION_POPULAR = "popular";
    final static String SELECTION_TOP_RATED = "top_rated";

    final static String PACKAGE_NAME = "com.example.lovebhardwaj.popularmoviesapp";

    private static final String RECYCLER_VIEW_BUNDLE_KEY = "RecyclerViewState";

    private ArrayList<JsonDataUtility.MovieItem> mMovieItems;

    private RecyclerView mRecyclerView;
    private MovieAdapter movieAdapter;

    private ProgressBar mProgressBar;

    private RecyclerView.LayoutManager mLayoutManager;

    private Parcelable recyclerViewState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar) findViewById(R.id.contentLoadingProgressBar);// Will work in the next stage
        mProgressBar.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black));
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView = (RecyclerView) findViewById(R.id.moviePosterRecyclerView);

        mLayoutManager = new GridLayoutManager(this, 2);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        movieAdapter = new MovieAdapter(mMovieItems, this, this);
        mRecyclerView.setAdapter(movieAdapter);

        if (savedInstanceState != null) {
            //Get the state of the recycler view
            recyclerViewState = savedInstanceState.getParcelable(RECYCLER_VIEW_BUNDLE_KEY);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: starts");

        String selectionCriteria;
        //Selection criteria for data to be downloaded
        if (getApplicationContext().getSharedPreferences(PACKAGE_NAME, MODE_PRIVATE) != null) {
            //Check to see if there is one
            selectionCriteria = getApplicationContext().getSharedPreferences(PACKAGE_NAME, MODE_PRIVATE).getString("SortOrder", SELECTION_POPULAR);
        } else {
            selectionCriteria = SELECTION_POPULAR;//Default selection criteria
        }

        String dataUrl = NetworkUtils.buildDataUrl(selectionCriteria).toString();

        JsonObjectRequest mJsonObjectRequest = new JsonObjectRequest(Request.Method.GET, dataUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    mProgressBar.setVisibility(View.GONE);
                    mMovieItems = JsonDataUtility.moviesData(response);
                    movieAdapter.loadNewData(mMovieItems);
                    if (recyclerViewState != null) {
                        //Check to see if there is a saved state
                        mLayoutManager.onRestoreInstanceState(recyclerViewState); //Load the state
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, R.string.error_internet, Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.GONE);
            }
        });
        mJsonObjectRequest.addMarker(TAG);// Marker to be used to cancel any volley request on stop
        NetworkUtils.VolleyUtility.getInstance(this).addToRequestQue(mJsonObjectRequest);


        Log.d(TAG, "onResume: ends");
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(RECYCLER_VIEW_BUNDLE_KEY, mLayoutManager.onSaveInstanceState());
    }

    @Override
    public void onPosterClickListener(int movieClicked) {
        Log.d(TAG, "onPosterClickListener: called");
        JsonDataUtility.MovieItem movieItem = mMovieItems.get(movieClicked);
        //Launch a new activity using the movieItem
        Intent intent = new Intent(this, MovieDetailsActivity.class);

        intent.putExtra("MovieItem", movieItem);//Send the selected movie and related movie item object
        startActivity(intent);//Start the activity
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

    @Override
    protected void onStop() {
        super.onStop();
        //Check to see if there is a volley request
        if (NetworkUtils.VolleyUtility.getInstance(this).getRequestQue() != null) {
            //If yes cancel the request
            NetworkUtils.VolleyUtility.getInstance(this).getRequestQue().cancelAll(TAG);
        }
    }
}
