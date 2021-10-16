package com.example.festivalapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ViewHolder> {

    private ArrayList<ReviewInfo> mDataset;
    private Activity activity;

    static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public ReviewListAdapter(Activity activity, ArrayList<ReviewInfo> myDataset) {
        mDataset = myDataset;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.reviews_holder_view, parent, false);
        final ViewHolder ViewHolder = new ViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return ViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        TextView tvWritedate = cardView.findViewById(R.id.tvWritedate);
        tvWritedate.setText(mDataset.get(position).getWritedate());
        TextView tvRTitle = cardView.findViewById(R.id.tvRvTitle);
        tvRTitle.setText(mDataset.get(position).getTitle());
        TextView tvRContent = cardView.findViewById(R.id.tvRContent);
        tvRContent.setText(mDataset.get(position).getContents());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}

