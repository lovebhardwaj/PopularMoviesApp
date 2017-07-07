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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.lovebhardwaj.popularmoviesapp.adapters.MovieListAdapter;
import com.example.lovebhardwaj.popularmoviesapp.data.MovieContract;
import com.example.lovebhardwaj.popularmoviesapp.utilities.JsonDataUtility;
import com.example.lovebhardwaj.popularmoviesapp.utilities.NetworkUtils;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MovieListAdapter.OnPosterClickListener, SharedPreferences.OnSharedPreferenceChangeListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "MainActivity";
    //Selection criteria, based on which data will be fetched
    private static final String SELECTION_POPULAR = "popular";
    private static final String SELECTION_TOP_RATED = "top_rated";
    private static final String SELECTION_FAVORITE = "favorites";
    //Keys
    private static final String SORT_ORDER_KEY = "sortOrder";
    final static String MOVIE_ITEM_KEY = "MovieItem";
    final static String FAVORITE = "favorite";

    private static final int FAVORITE_LOADER_ID = 619;
    private static final String RECYCLER_VIEW_BUNDLE_KEY = "RecyclerViewState";
    private static final String OLD_PREFERENCE = "OldPreference";
    private static final String POSITION_KEY = "PositionKey";

    //List to store the relevant movies
    private static ArrayList<JsonDataUtility.MovieItem> mMovieItems;
    private static ArrayList<JsonDataUtility.MovieItem> mFavoriteList;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private Parcelable recyclerViewState;

    private MovieListAdapter mMovieListAdapter;

    private ProgressBar mProgressBar;
    private SharedPreferences mSharedPreferences;

    private String currentPreference;//Variable to store sharedPreference String

    private static Bundle saveRecyclerState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar) findViewById(R.id.contentLoadingProgressBar);
        mProgressBar.setVisibility(View.VISIBLE);

        mLayoutManager = new GridLayoutManager(this, numberOfColumns());

        mRecyclerView = (RecyclerView) findViewById(R.id.moviePosterRecyclerView);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMovieListAdapter = new MovieListAdapter(mMovieItems, this, this);
        mRecyclerView.setAdapter(mMovieListAdapter);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        currentPreference = mSharedPreferences.getString(SORT_ORDER_KEY, SELECTION_POPULAR);

        //Method to run the default code to load the UI
        defaultList();

    }

    private void defaultList() {
        String selectionCriteria = SELECTION_POPULAR;
        //Selection criteria for data to be downloaded

        if (mSharedPreferences != null) {
            switch (selectionCriteria = mSharedPreferences.getString(SORT_ORDER_KEY, SELECTION_POPULAR)) {
                case SELECTION_POPULAR:
                    loadDataFromApi(selectionCriteria);
                    break;
                case SELECTION_TOP_RATED:
                    loadDataFromApi(selectionCriteria);
                    break;
                case SELECTION_FAVORITE:
                    getSupportLoaderManager().initLoader(FAVORITE_LOADER_ID, null, this);
                    break;
            }
        } else {
            //Load the default list that is the popular movies, if not preference exist
            loadDataFromApi(selectionCriteria);
        }
    }

    @Override
    protected void onResume() {
        String selectionCriteria = mSharedPreferences.getString(SORT_ORDER_KEY, SELECTION_POPULAR);

        //Check to see if same after returning from another activity or from landscape mode
        if (saveRecyclerState != null) {
            //If same list was selected again restore the state
            String oldSelection = saveRecyclerState.getString(OLD_PREFERENCE);
            if (selectionCriteria.equals(oldSelection)) {
                recyclerViewState = saveRecyclerState.getParcelable(RECYCLER_VIEW_BUNDLE_KEY);
                mLayoutManager.onRestoreInstanceState(recyclerViewState); //Load the state
            }
            int scrollPosition = saveRecyclerState.getInt(POSITION_KEY);

            if (scrollPosition == mMovieListAdapter.getItemCount()-1){
                mRecyclerView.smoothScrollToPosition(scrollPosition);
            }
        }

        if (selectionCriteria.equals(SELECTION_FAVORITE)) { //Only restart the loader when favorite list selected
            //User may have deleted something from movie list, need to update the list
            getSupportLoaderManager().restartLoader(FAVORITE_LOADER_ID, null, this);
        }
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        saveRecyclerState = new Bundle();
        saveRecyclerState.putParcelable(RECYCLER_VIEW_BUNDLE_KEY, mLayoutManager.onSaveInstanceState());
        saveRecyclerState.putString(OLD_PREFERENCE, mSharedPreferences.getString(SORT_ORDER_KEY, SELECTION_POPULAR));
        saveRecyclerState.putInt(POSITION_KEY, ((GridLayoutManager) mLayoutManager).findLastVisibleItemPosition());

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //Check the preference selected and call the related method
        switch (sharedPreferences.getString(SORT_ORDER_KEY, SELECTION_POPULAR)) {
            case SELECTION_POPULAR:
                currentPreference = SELECTION_POPULAR;
                loadDataFromApi(SELECTION_POPULAR);
                break;
            case SELECTION_TOP_RATED:
                currentPreference = SELECTION_TOP_RATED;
                loadDataFromApi(SELECTION_TOP_RATED);
                break;
            case SELECTION_FAVORITE:
                currentPreference = SELECTION_FAVORITE;
                getSupportLoaderManager().restartLoader(FAVORITE_LOADER_ID, null, this);
                break;
            default:
                loadDataFromApi(SELECTION_POPULAR);
                break;
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(OLD_PREFERENCE, mSharedPreferences.getString(SORT_ORDER_KEY, SELECTION_POPULAR));

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {

            if (savedInstanceState.containsKey(OLD_PREFERENCE)) {
                currentPreference = savedInstanceState.getString(OLD_PREFERENCE);
            }
        }

    }

    @Override
    public void onPosterClickListener(int movieClicked) {
        JsonDataUtility.MovieItem movieItem = null;
        //Launch a new activity using the movieItem, that is the current item
        boolean isFavorite = false;

        String selectionCriteria = mSharedPreferences.getString(SORT_ORDER_KEY, SELECTION_POPULAR);

        switch (selectionCriteria) {
            case SELECTION_POPULAR:
                movieItem = mMovieItems.get(movieClicked);
                isFavorite = checkIfFavorite(movieItem);
                break;
            case SELECTION_TOP_RATED:
                movieItem = mMovieItems.get(movieClicked);
                isFavorite = checkIfFavorite(movieItem);
                break;
            case SELECTION_FAVORITE:
                movieItem = mFavoriteList.get(movieClicked);
                isFavorite = checkIfFavorite(movieItem);
                break;
        }

        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra(MOVIE_ITEM_KEY, movieItem);//Send the selected movie and related movie item object
        //If in the favorite list mark it as favorite
        if (isFavorite) intent.putExtra(FAVORITE, true);
        startActivity(intent);//Start the activity
    }

    private boolean checkIfFavorite(JsonDataUtility.MovieItem item) {
        //Method to check if favorite, if it is then pass the value. So that the detail view can work accordingly
        boolean status = false;
        if (mFavoriteList != null) {
            for (JsonDataUtility.MovieItem listItem : mFavoriteList) {
                if (listItem.getMovieId().equals(item.getMovieId())) {
                    status = true;
                }
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Cursor loader class to load data from the database, returning a cursor
        return new AsyncTaskLoader<Cursor>(this) {
            //Initialize the cursor
            Cursor mFavoriteListCursor = null;

            @Override
            protected void onStartLoading() {
                if (mFavoriteListCursor != null) {
                    deliverResult(mFavoriteListCursor);
                } else {
                    forceLoad();
                }
                super.onStartLoading();
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                            null, null, null, MovieContract.MovieEntry.MOVIE_ID);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(Cursor data) {
                //Load the data in the cursor
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

    /*
    Two helper method to facilitate the loading the data from the api or
    the database. loadDataFromApi and loadDatabase from database
    are called depending on the selection made
     */
    private void loadDataFromApi(String selectionCriteria) {
        String dataUrl = NetworkUtils.buildDataUrl(selectionCriteria).toString();

        JsonObjectRequest mJsonObjectRequest = new JsonObjectRequest(Request.Method.GET, dataUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    mMovieItems = JsonDataUtility.moviesData(response);
                    mMovieListAdapter.loadNewData(mMovieItems);

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, R.string.error_response, Toast.LENGTH_SHORT).show();
            }
        });
        mJsonObjectRequest.addMarker(TAG);// Marker to be used to cancel any volley request on stop
        NetworkUtils.VolleyUtility.getInstance(this).addToRequestQue(mJsonObjectRequest);
        mProgressBar.setVisibility(View.GONE);
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
        if (favoriteList.isEmpty() && currentPreference.equals(SELECTION_FAVORITE)) {
            Toast.makeText(this, R.string.empty_favorite_List, Toast.LENGTH_SHORT).show();
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
