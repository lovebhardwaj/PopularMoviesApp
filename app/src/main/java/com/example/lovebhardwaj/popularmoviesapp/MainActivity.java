package com.example.lovebhardwaj.popularmoviesapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
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
import com.example.lovebhardwaj.popularmoviesapp.Adapters.MovieListAdapter;
import com.example.lovebhardwaj.popularmoviesapp.Data.MovieContract;
import com.example.lovebhardwaj.popularmoviesapp.Utilities.JsonDataUtility;
import com.example.lovebhardwaj.popularmoviesapp.Utilities.NetworkUtils;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;

public class MainActivity extends AppCompatActivity implements MovieListAdapter.OnPosterClickListener, SharedPreferences.OnSharedPreferenceChangeListener,
        LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = "MainActivity";
    //Selection criteria, based on which data will be fetched
    final static String SELECTION_POPULAR = "popular";
    final static String SELECTION_TOP_RATED = "top_rated";
    final static String SELECTION_FAVORITE = "favorites";
    final static String SORT_ORDER_KEY = "sortOrder";
    final static String MOVIE_ITEM_KEY = "MovieItem";
    final static String FAVORITE = "favorite";

    private static final int FAVORITE_LOADER_ID = 619;
    private static final String RECYCLER_VIEW_BUNDLE_KEY = "RecyclerViewState";

    private ArrayList<JsonDataUtility.MovieItem> mMovieItems;
    private ArrayList<JsonDataUtility.MovieItem> mFavoriteList;

    @BindView(R.id.moviePosterRecyclerView) RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private Parcelable recyclerViewState;

    private MovieListAdapter mMovieListAdapter;

    private ProgressBar mProgressBar;
    private SharedPreferences mSharedPreferences;

    private String statusString;
    private boolean isSameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar) findViewById(R.id.contentLoadingProgressBar);
        mProgressBar.setVisibility(View.VISIBLE);

        mLayoutManager = new GridLayoutManager(this, numberOfColumns());

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMovieListAdapter = new MovieListAdapter(mMovieItems, this, this);
        mRecyclerView.setAdapter(mMovieListAdapter);

        if (savedInstanceState != null) {
            //Get the state of the recycler view
            if (savedInstanceState.containsKey(RECYCLER_VIEW_BUNDLE_KEY)) {
                recyclerViewState = savedInstanceState.getParcelable(RECYCLER_VIEW_BUNDLE_KEY);
            }
        }
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        statusString = mSharedPreferences.getString(SORT_ORDER_KEY, SELECTION_POPULAR);
        getSupportLoaderManager().initLoader(FAVORITE_LOADER_ID, null, this);
        defaultList();
        isSameList = false;
    }

    @Override
    protected void onResume() {
        String selectionCriteria = mSharedPreferences.getString(SORT_ORDER_KEY, SELECTION_POPULAR);
        if (selectionCriteria.equals(statusString)) isSameList = true;
        if (selectionCriteria.equals(SELECTION_FAVORITE)) { //Only restart the loader when favorite list selected
            getSupportLoaderManager().restartLoader(FAVORITE_LOADER_ID, null, this);
        }
        super.onResume();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //Check the preference selected and call the related method
        switch (sharedPreferences.getString(SORT_ORDER_KEY, SELECTION_POPULAR)) {
            case SELECTION_POPULAR:
                loadDataFromApi(SELECTION_POPULAR);
                break;
            case SELECTION_TOP_RATED:
                loadDataFromApi(SELECTION_TOP_RATED);
                break;
            case SELECTION_FAVORITE:
                getSupportLoaderManager().initLoader(FAVORITE_LOADER_ID, null, this);
                break;
            default:
                loadDataFromApi(SELECTION_POPULAR);
                break;
        }


        if (recyclerViewState != null && isSameList) {//If same list was selected again
            //Restore the state
            //Check to see if there is a saved state
            mLayoutManager.onRestoreInstanceState(recyclerViewState); //Load the state
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(RECYCLER_VIEW_BUNDLE_KEY, mLayoutManager.onSaveInstanceState());
    }

    @Override
    public void onPosterClickListener(int movieClicked) {

        JsonDataUtility.MovieItem movieItem = null;
        //Launch a new activity using the movieItem
        boolean isFavorite = false;

        String selectionCriteria = mSharedPreferences.getString(SORT_ORDER_KEY, SELECTION_POPULAR);
        if (selectionCriteria.equals(SELECTION_POPULAR) || selectionCriteria.equals(SELECTION_TOP_RATED)){
            movieItem = mMovieItems.get(movieClicked);
            isFavorite = checkIfFavorite(movieItem);
        }else if (selectionCriteria.equals(SELECTION_FAVORITE)){
            movieItem = mFavoriteList.get(movieClicked);
            isFavorite = checkIfFavorite(movieItem);
        }

        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra(MOVIE_ITEM_KEY, movieItem);//Send the selected movie and related movie item object
        if (isFavorite) intent.putExtra(FAVORITE, true);
        startActivity(intent);//Start the activity
    }

    private boolean checkIfFavorite(JsonDataUtility.MovieItem item){
        boolean status = false;
        for (JsonDataUtility.MovieItem listItem : mFavoriteList){
            if (listItem.getMovieId().equals(item.getMovieId())){
                status = true;
            }
        }
        return status;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    private void defaultList() {
        String selectionCriteria;
        //Selection criteria for data to be downloaded

        //No need to check if preference is empty, if it is the default value is used
        selectionCriteria = mSharedPreferences.getString(SORT_ORDER_KEY, SELECTION_POPULAR);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        if (selectionCriteria.equals(SELECTION_POPULAR) || selectionCriteria.equals(SELECTION_TOP_RATED)) {
            loadDataFromApi(selectionCriteria);
        }else if (selectionCriteria.equals(SELECTION_FAVORITE)){
            getSupportLoaderManager().initLoader(FAVORITE_LOADER_ID, null, this);
        }

        //Else get the list of favorites movies from the data base that will be created
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            Cursor mFavoriteListCursor = null;

            @Override
            protected void onStartLoading() {
                if (mFavoriteListCursor != null){
                    deliverResult(mFavoriteListCursor);
                }else {
                    forceLoad();
                }
                super.onStartLoading();
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, MovieContract.MovieEntry.MOVIE_ID);
                }catch (Exception e){
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(Cursor data) {
                mFavoriteListCursor = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        loadFavorite(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loadFavorite(null);
    }

    private void loadDataFromApi(String selectionCriteria) {
        String dataUrl = NetworkUtils.buildDataUrl(selectionCriteria).toString();

        JsonObjectRequest mJsonObjectRequest = new JsonObjectRequest(Request.Method.GET, dataUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    mProgressBar.setVisibility(View.GONE);
                    mMovieItems = JsonDataUtility.moviesData(response);
                    mMovieListAdapter.loadNewData(mMovieItems);

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
    }



    private void loadFavorite(Cursor data) {
        ArrayList<JsonDataUtility.MovieItem> favoriteList = new ArrayList<>();
        while (data.moveToNext()) {
            JsonDataUtility.MovieItem loadedMovieItem = new JsonDataUtility.MovieItem(data.getString(data.getColumnIndex(MovieContract.MovieEntry.MOVIE_ID)),
                    data.getString(data.getColumnIndex(MovieContract.MovieEntry.MOVIE_TITLE)), data.getString(data.getColumnIndex(MovieContract.MovieEntry.MOVIE_POSTER_PATH)),
                    data.getString(data.getColumnIndex(MovieContract.MovieEntry.MOVIE_PLOT)), data.getString(data.getColumnIndex(MovieContract.MovieEntry.MOVIE_RATING)),
                    data.getString(data.getColumnIndex(MovieContract.MovieEntry.MOVIE_RELEASE_DATE)));
            favoriteList.add(loadedMovieItem);
        }
        mFavoriteList = favoriteList;
        mMovieListAdapter.loadNewData(mFavoriteList);
        mProgressBar.setVisibility(View.GONE);
    }

    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthDivider = Integer.parseInt(NetworkUtils.BASE_POSTER_WIDTH);
        int screenWidth = displayMetrics.widthPixels;
        int numColumns = screenWidth / widthDivider;
        if (numColumns < 2) return 2;
        return numColumns;
    }
}
