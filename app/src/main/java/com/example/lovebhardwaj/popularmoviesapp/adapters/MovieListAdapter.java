package com.example.lovebhardwaj.popularmoviesapp.adapters;


import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.lovebhardwaj.popularmoviesapp.R;
import com.example.lovebhardwaj.popularmoviesapp.utilities.JsonDataUtility;
import com.example.lovebhardwaj.popularmoviesapp.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter to populate recycler view in the main activity
 */

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MoveItemViewHolder> {
    private ArrayList<JsonDataUtility.MovieItem> mMovieItems;
    private final Context mContext;
    private final OnPosterClickListener mOnPosterClickListener;

    //Callback interface for click handling
    public interface OnPosterClickListener{
        void onPosterClickListener(int movieClicked);
    }

    public MovieListAdapter(ArrayList<JsonDataUtility.MovieItem> movieItems, Context context, OnPosterClickListener onPosterClickListener) {
        mMovieItems = movieItems;
        mContext = context;
        this.mOnPosterClickListener = onPosterClickListener;
    }

    @Override
    public MovieListAdapter.MoveItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MoveItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.moive_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MovieListAdapter.MoveItemViewHolder holder, int position) {
        if (!mMovieItems.isEmpty() && mMovieItems.size()>0) {
            JsonDataUtility.MovieItem movieItem = mMovieItems.get(position);
            Uri imageUri = NetworkUtils.buildImageUrl(movieItem.getPosterPath());
            Picasso.with(mContext).load(imageUri)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(holder.mImageView);
        }
    }

    @Override
    public int getItemCount() {
        if (mMovieItems != null && mMovieItems.size() > 0) {
            return mMovieItems.size();
        }
        return 0;
    }

    public void loadNewData(ArrayList<JsonDataUtility.MovieItem> newArrayList){
        mMovieItems = newArrayList;
        notifyDataSetChanged();
    }


    class MoveItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.posterListImageView) ImageView mImageView;


        MoveItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int movieClicked = getAdapterPosition();
            mOnPosterClickListener.onPosterClickListener(movieClicked);
        }
    }

}
