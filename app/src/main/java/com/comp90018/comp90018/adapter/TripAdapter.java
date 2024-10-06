// app/src/main/java/com/comp90018/comp90018/adapter/TripAdapter.java
package com.comp90018.comp90018.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
// 导入必要的类
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.comp90018.comp90018.R;
import com.comp90018.comp90018.model.Trip;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private Context context;
    private List<Trip> tripList;
    private OnItemClickListener listener;

    // 点击事件接口
    public interface OnItemClickListener {
        void onItemClick(Trip trip);
    }

    // 构造函数
    public TripAdapter(Context context, List<Trip> tripList, OnItemClickListener listener) {
        this.context = context;
        this.tripList = tripList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 填充 item_trip.xml 布局
        View view = LayoutInflater.from(context).inflate(R.layout.item_trip, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        // 获取当前 Trip 对象
        Trip trip = tripList.get(position);

        // 设置数据到视图
        holder.textViewTitle.setText(trip.getTitle());
        holder.textViewDates.setText(trip.getStartDate() + " - " + trip.getEndDate());
        holder.textViewLocation.setText(trip.getLocation());

        // 设置本地图片资源
        holder.imageViewThumbnail.setImageResource(trip.getThumbnailResId());

        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(trip);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    // ViewHolder 类
    public class TripViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewThumbnail;
        TextView textViewTitle, textViewDates, textViewLocation;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewThumbnail = itemView.findViewById(R.id.imageView_trip_thumbnail);
            textViewTitle = itemView.findViewById(R.id.textView_trip_title);
            textViewDates = itemView.findViewById(R.id.textView_trip_dates);
            textViewLocation = itemView.findViewById(R.id.textView_trip_location);
        }
    }
}
