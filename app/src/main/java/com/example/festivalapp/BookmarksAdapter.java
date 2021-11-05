package com.example.festivalapp;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

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
        cardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //클릭 시, 축제 상세 정보 페이지로 이동
                //*조건 - 축제 있을 때!
                Intent intent = new Intent(activity, DetailInfoActivity.class);
                intent.putExtra("contentid", mDataset.get(position).getContentid());
                activity.startActivity(intent);
            }
        });

        //imgvThumbnail
        String imgUrl = mDataset.get(position).getFirstimage();
        ImageView imgvImage = cardView.findViewById(R.id.imgvThumbnail);
        Glide.with(cardView).load(imgUrl).override(cardView.getWidth(), cardView.getHeight()).into(imgvImage);

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

