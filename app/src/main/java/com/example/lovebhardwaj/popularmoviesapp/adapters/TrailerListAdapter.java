package com.example.lovebhardwaj.popularmoviesapp.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lovebhardwaj.popularmoviesapp.R;
import com.example.lovebhardwaj.popularmoviesapp.utilities.JsonDataUtility;
import com.example.lovebhardwaj.popularmoviesapp.utilities.NetworkUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter class to populate the trailer recycler view
 */

public class TrailerListAdapter extends RecyclerView.Adapter<TrailerListAdapter.TrailerViewHolder> {
    private ArrayList<JsonDataUtility.TrailerInfo> mTrailerList;

    public TrailerListAdapter(ArrayList<JsonDataUtility.TrailerInfo> trailerList) {
        mTrailerList = trailerList;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TrailerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_list_item, parent,false));
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        JsonDataUtility.TrailerInfo trailerInfo = mTrailerList.get(position);
            holder.trailerTitleTextView.setText(trailerInfo.getTrailerTitle());
    }

    @Override
    public int getItemCount() {
        if (mTrailerList != null && mTrailerList.size() > 0) {
            return mTrailerList.size();
        }
        return 0;
    }

    public void loadNewData(ArrayList<JsonDataUtility.TrailerInfo> trailerList){
        mTrailerList = trailerList;
        notifyDataSetChanged();
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.tv_trailerTitle) TextView trailerTitleTextView;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int trailerClicked = getAdapterPosition();
            JsonDataUtility.TrailerInfo trailerInfo = mTrailerList.get(trailerClicked);
            Intent youTubeIntent = new Intent(Intent.ACTION_VIEW, NetworkUtils.youtubeUri(trailerInfo.getTrailerKey()));

            if (youTubeIntent.resolveActivity(v.getContext().getPackageManager()) != null){
                //Open the link if youtube or browser
                v.getContext().startActivity(youTubeIntent);
            }
        }
    }
}
