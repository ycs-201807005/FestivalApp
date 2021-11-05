package com.example.festivalapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class DialogRecyclerAdapter extends RecyclerView.Adapter<DialogRecyclerAdapter.ViewHolder>{
    private ArrayList<MarkerInfo> mDataset;
    private Activity activity;
    private MainActivity mainActivity;

    static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public DialogRecyclerAdapter(/*Activity activity,*/ Context mainActivityClass, ArrayList<MarkerInfo> myDataset) {
        mDataset = myDataset;
        this.activity = (Activity) mainActivityClass;
        mainActivity = (MainActivity) activity;
    }

    @Override
    public ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_markerinfo_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adaptPostion = viewHolder.getAdapterPosition();
                Log.e("실행", String.valueOf(adaptPostion));
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setTag(position);

        CardView cardView = holder.cardView;
        cardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //지도 지우기
                mainActivity.clearMap();
                //값-화면 전환
                Intent intent = new Intent(activity, DetailInfoActivity.class);
                intent.putExtra("contentid", mDataset.get(position).getContentid());
                activity.startActivity(intent);
                Log.e("ViewHolder onCreateViewHolder", ""+mDataset.get(position).getContentid());
            }
        });

        //imgvThumbnail
        String imgUrl = mDataset.get(position).getFirstimage();
        ImageView imgvThumbnail = cardView.findViewById(R.id.imgvThumbnail);
        Glide.with(cardView).load(imgUrl).override(cardView.getWidth(), cardView.getHeight()).into(imgvThumbnail);

        TextView textViewRecyclerItem1 = cardView.findViewById(R.id.textViewRecyclerItem1);
        textViewRecyclerItem1.setText(mDataset.get(position).getTitle());
        TextView textViewRecyclerItem2 = cardView.findViewById(R.id.textViewRecyclerItem2);
        textViewRecyclerItem2.setText(mDataset.get(position).getEventplace());

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
