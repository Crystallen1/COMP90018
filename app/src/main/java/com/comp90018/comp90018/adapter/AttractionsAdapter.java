// File: com/comp90018/comp90018/adapter/AttractionsAdapter.java

package com.comp90018.comp90018.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.comp90018.comp90018.R;
import com.comp90018.comp90018.model.Journey;

import java.util.List;

public class AttractionsAdapter extends RecyclerView.Adapter<AttractionsAdapter.AttractionViewHolder> {

    private Context context;
    private List<Journey> journeyList;
    private NavController navController;

    // Constructor
    public AttractionsAdapter(Context context, List<Journey> journeyList, NavController navController) {
        this.context = context;
        this.journeyList = journeyList;
        this.navController = navController; // 接收 NavController
    }

    // ViewHolder class
    public static class AttractionViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewAttraction;
        public TextView textViewName;
        public TextView textViewNotes;

        public AttractionViewHolder(View itemView) {
            super(itemView);
            imageViewAttraction = itemView.findViewById(R.id.imageView_attraction);
            textViewName = itemView.findViewById(R.id.textView_name);
            textViewNotes = itemView.findViewById(R.id.textView_location);
        }
    }

    @Override
    public AttractionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_attraction, parent, false);
        return new AttractionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AttractionViewHolder holder, int position) {
        // Get the current Journey object
        Journey journey = journeyList.get(position);

        // Set the attraction name and notes
        holder.textViewName.setText(journey.getName());
        holder.textViewNotes.setText(journey.getNotes());
        holder.itemView.setOnClickListener(v->navigationToDetail());
        // 设置点击事件，导航到 DetailFragment
        holder.itemView.setOnClickListener(v -> {
            // 设置 Bundle 传递参数（可选）
            Bundle bundle = new Bundle();
            bundle.putParcelable("journey", journey);  // 传递自定义对象

            // 使用 NavController 进行导航
            navController.navigate(R.id.action_attraction_to_attraction_detail, bundle);
        });

        // Load the image using Glide
        // You can add placeholder and error images as needed
        Glide.with(context)
                .load(journey.getImageUrl())
                .placeholder(R.drawable.ic_calendar) // Optional placeholder
                .error(R.drawable.ic_back) // Optional error image
                .into(holder.imageViewAttraction);
    }

    private void navigationToDetail() {

    }

    @Override
    public int getItemCount() {
        return journeyList.size();
    }

    // Optional: Method to update the data
    public void updateJourneys(List<Journey> newJourneys) {
        journeyList.clear();
        journeyList.addAll(newJourneys);
        notifyDataSetChanged();
    }
}
