package com.example.lovebhardwaj.popularmoviesapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "SettingsActivity";
    private List<String> spinnerSelection;
    private Spinner sortOrderSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(getString(R.string.settings));

        if (this.getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        spinnerSelection = new ArrayList<>();
        sortOrderSpinner = (Spinner) findViewById(R.id.sortOrderSpinner);
        spinnerSelection.add("Popular Movies");
        spinnerSelection.add("Top Rated Movies");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, spinnerSelection);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortOrderSpinner.setAdapter(spinnerAdapter);
        sortOrderSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemSelected: called");
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(MainActivity.PACKAGE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Log.d(TAG, "onItemSelected: " + parent.getItemAtPosition(position));
        Log.d(TAG, "onItemSelected: position " + position);
        switch (position){
            case 0:
                editor.putString("SortOrder", MainActivity.SELECTION_POPULAR);
                editor.apply();
                break;
            case 1:
                editor.putString("SortOrder", MainActivity.SELECTION_TOP_RATED);
                editor.apply();
                break;
            default:
                editor.putString("SortOrder", MainActivity.SELECTION_POPULAR);
                editor.apply();
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
