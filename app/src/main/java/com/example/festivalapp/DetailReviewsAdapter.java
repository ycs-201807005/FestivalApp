package com.example.festivalapp;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class DetailReviewsAdapter extends RecyclerView.Adapter<DetailReviewsAdapter.ViewHolder>  {
    private static final String TAG = "DetailReviewsAdapter";

    private ArrayList<ReviewInfo> mDataset;
    private Activity activity;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public DetailReviewsAdapter(Activity activity, ArrayList<ReviewInfo> myDataset) {
        this.mDataset = myDataset;
        this.activity = activity;
        Log.e(TAG, "DetailReviewsAdapter() mDataset " + mDataset);
        Log.e(TAG, "DetailReviewsAdapter() activity " + activity);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_reviews_adapter, parent, false);
        final ViewHolder ViewHolder = new ViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        Log.e(TAG, "DetailReviewsAdapter() - cardView " + cardView);
        Log.e(TAG, "DetailReviewsAdapter() - ViewHolder " + ViewHolder);
        return ViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //클릭 시, Review Read
                Intent intent = new Intent(activity, ReviewReadActivity.class);
                intent.putExtra("reviewid", mDataset.get(position).getReviewid());
                intent.putExtra("activity", "DetailInfo");
                activity.startActivity(intent);
            }
        });

        Log.e(TAG, "reviewid: " + mDataset.get(position).getReviewid());

        //write_date
        TextView tvWritedate = cardView.findViewById(R.id.tvWritedate);
        tvWritedate.setText(mDataset.get(position).getWritedate());
        //star_rating
        RatingBar ratingBar = cardView.findViewById(R.id.ratingBar);
        ratingBar.setRating((float) mDataset.get(position).getRating());

        //festival_writername
        /* 사용자 이름 가져오기 */
        TextView tvWritername = cardView.findViewById(R.id.tvWritername);
        firestore.collection("users").document(mDataset.get(position).getWriter()).get()
                .addOnCompleteListener((task) -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            String wrtiername = (String) document.get("name");
                            Log.e(TAG,"wrtiername : " + wrtiername);
                            tvWritername.setText(wrtiername);
                        }
                    } else {
                        Log.e(TAG,"wrtiername - query failed");                                            }
                });

        //review_content_1line
        TextView tvRContent = cardView.findViewById(R.id.tvRContent);
        tvRContent.setText(mDataset.get(position).getContents());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}