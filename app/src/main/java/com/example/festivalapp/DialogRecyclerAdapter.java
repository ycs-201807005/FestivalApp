package com.example.festivalapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DialogRecyclerAdapter extends RecyclerView.Adapter<DialogRecyclerAdapter.ViewHolder>{
    private ArrayList<MarkerInfo> mDataset;
    //private Activity activity;

    static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public DialogRecyclerAdapter(/*Activity activity,*/ ArrayList<MarkerInfo> myDataset) {
        mDataset = myDataset;
        //this.activity = activity;
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
