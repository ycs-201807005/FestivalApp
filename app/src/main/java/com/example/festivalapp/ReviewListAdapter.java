package com.example.festivalapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ViewHolder> {
    private static final String TAG = "ReviewListAdapter";

    private ArrayList<ReviewInfo> mDataset;
    private Activity activity;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //클릭 시, Review Read
                Intent intent = new Intent(activity, ReviewReadActivity.class);
                intent.putExtra("reviewid", mDataset.get(position).getReviewid());
                activity.startActivity(intent);
            }
        });

        /* 리뷰 삭제 하기 */
        TextView btnRvDelete = (TextView)cardView.findViewById(R.id.btnRvDelete);
        btnRvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Log.e(TAG,"showDialogForLocationSave() - 확인");
                        db.collection("reviews").document(mDataset.get(position).getReviewid()).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                        Log.e(TAG, "리뷰 삭제");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "Error deleting document", e);
                                    }
                                });
                    }
                });
                builder.show();
            }
        });

        //wrtie_date
        TextView tvWritedate = cardView.findViewById(R.id.tvWritedate);
        tvWritedate.setText(mDataset.get(position).getWritedate());
        //star_rating
        RatingBar ratingBar = cardView.findViewById(R.id.ratingBar);
        ratingBar.setRating((float) mDataset.get(position).getRating());
        //festival_title
        TextView tvRTitle = cardView.findViewById(R.id.tvRvTitle);
        tvRTitle.setText(mDataset.get(position).getTitle());
        //review_content_1line
        TextView tvRContent = cardView.findViewById(R.id.tvRContent);
        tvRContent.setText(mDataset.get(position).getContents());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}

