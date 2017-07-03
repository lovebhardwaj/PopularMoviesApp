package com.example.lovebhardwaj.popularmoviesapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lovebhardwaj.popularmoviesapp.R;
import com.example.lovebhardwaj.popularmoviesapp.utilities.JsonDataUtility;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter class to poplate the review recycler view
 */

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ReviewViewHolder> {
    private ArrayList<JsonDataUtility.ReviewInfo> mReviewList;
    public ReviewListAdapter(ArrayList<JsonDataUtility.ReviewInfo> reviewList){
        mReviewList = reviewList;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ReviewViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.review_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        JsonDataUtility.ReviewInfo reviewInfo = mReviewList.get(position);

            holder.authorTextView.setText(reviewInfo.getAuthor());
            holder.contentTextView.setText(reviewInfo.getContent());

    }

    @Override
    public int getItemCount() {
        if (mReviewList != null && mReviewList.size() > 0) {
            return mReviewList.size();
        }
        return 0;
    }

    public void loadNewData(ArrayList<JsonDataUtility.ReviewInfo> reviewList){
        mReviewList = reviewList;
        notifyDataSetChanged();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_author)
        TextView authorTextView;
        @BindView(R.id.tv_content)
        TextView contentTextView;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
