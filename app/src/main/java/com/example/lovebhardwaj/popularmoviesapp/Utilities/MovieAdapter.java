package com.example.lovebhardwaj.popularmoviesapp.Utilities;


import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.lovebhardwaj.popularmoviesapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by love on 6/12/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MoveItemViewHolder> {
    private ArrayList<JsonDataUtility.MovieItem> mMovieItems;
    private final Context mContext;
    private final OnPosterClickListener mOnPosterClickListener;

    //Callback interface for click handling
    public interface OnPosterClickListener{
        void onPosterClickListener(int movieClicked);
    }

    public MovieAdapter(ArrayList<JsonDataUtility.MovieItem> movieItems, Context context, OnPosterClickListener onPosterClickListener) {
        mMovieItems = movieItems;
        mContext = context;
        this.mOnPosterClickListener = onPosterClickListener;
    }

    @Override
    public MovieAdapter.MoveItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MoveItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_poster_list, parent, false));
    }

    @Override
    public void onBindViewHolder(MovieAdapter.MoveItemViewHolder holder, int position) {
        JsonDataUtility.MovieItem movieItem = mMovieItems.get(position);
        Uri imageUri = NetworkUtils.buildImageUrl(movieItem.getPosterPath());
        Picasso.with(mContext).load(imageUri)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(holder.mImageView);
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
        final ImageView mImageView;

        MoveItemViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.posterListImageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int movieClicked = getAdapterPosition();
            mOnPosterClickListener.onPosterClickListener(movieClicked);
        }
    }
}
