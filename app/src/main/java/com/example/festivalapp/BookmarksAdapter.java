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

public class BookmarksAdapter extends RecyclerView.Adapter<BookmarksAdapter.ViewHolder> {

    private ArrayList<MarkerInfo> mDataset;
    private Activity activity;

    static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public BookmarksAdapter(Activity activity, ArrayList<MarkerInfo> myDataset) {
        mDataset = myDataset;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.bmarks_holder_view, parent, false);
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
        TextView bmarksTitle = cardView.findViewById(R.id.bmarksTitle);
        bmarksTitle.setText(mDataset.get(position).getTitle());
        TextView bmarksevtPlace = cardView.findViewById(R.id.bmarksevtPlace);
        bmarksevtPlace.setText(mDataset.get(position).getEventplace());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}

