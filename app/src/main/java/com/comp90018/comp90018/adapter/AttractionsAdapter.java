// File: com/comp90018/comp90018/adapter/AttractionsAdapter.java

package com.comp90018.comp90018.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.comp90018.comp90018.R;
import com.comp90018.comp90018.model.Journey;

import java.util.List;

public class AttractionsAdapter extends RecyclerView.Adapter<AttractionsAdapter.AttractionViewHolder> {

    private Context context;
    private List<Journey> journeyList;

    // Constructor
    public AttractionsAdapter(Context context, List<Journey> journeyList) {
        this.context = context;
        this.journeyList = journeyList;
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


        // Load the image using Glide
        // You can add placeholder and error images as needed
        Glide.with(context)
                .load(journey.getImageUrl())
                .placeholder(R.drawable.ic_calendar) // Optional placeholder
                .error(R.drawable.ic_back) // Optional error image
                .into(holder.imageViewAttraction);
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
