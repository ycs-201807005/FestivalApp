package com.example.festivalapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ListsAdapter extends RecyclerView.Adapter<ListsAdapter.ViewHolder> {
    private ArrayList<FestivalInfo> mDataset;
    private Activity activity;
    private MainActivity mainActivity;

    static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public ListsAdapter(Activity activity, ArrayList<FestivalInfo> myDataset) {
        mDataset = myDataset;
        this.activity = activity;
        mainActivity = (MainActivity) activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.festivals_holder_view, parent, false);
        final ViewHolder ViewHolder = new ViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return ViewHolder;
    }

    @NonNull
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        cardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mainActivity.clearMap();
                Intent intent = new Intent(activity, DetailInfoActivity.class);
                intent.putExtra("contentid", mDataset.get(position).getContentid());
                //지도 지우기
                activity.startActivity(intent);
                Log.e("ViewHolder onCreateViewHolder", ""+mDataset.get(position).getContentid());
            }
        });

        String imgUrl = mDataset.get(position).getImage();
        ImageView imgvImage = cardView.findViewById(R.id.imgvImage);

        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        imgvImage.getLayoutParams().height = size.y/3;

        Glide.with(cardView).load(imgUrl).override(cardView.getWidth(), cardView.getHeight()).into(imgvImage);

        TextView tvTitle = cardView.findViewById(R.id.tvTitle);
        tvTitle.setText(mDataset.get(position).getTitle());

        TextView tvEventstartdate = cardView.findViewById(R.id.tvEventdate);
        String eventdate = mDataset.get(position).getEventstartdate() + " ~ " + mDataset.get(position).getEventenddate();
        tvEventstartdate.setText(eventdate);

        TextView tvEventplace = cardView.findViewById(R.id.tvEventplace);
        tvEventplace.setText(mDataset.get(position).getEventplace());

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
