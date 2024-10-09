// app/src/main/java/com/comp90018/comp90018/adapter/TripAdapter.java
package com.comp90018.comp90018.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
// 导入必要的类
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;
import com.comp90018.comp90018.R;
import com.comp90018.comp90018.model.DayPlan;
import com.comp90018.comp90018.model.TotalPlan;
import com.comp90018.comp90018.model.Trip;
import com.comp90018.comp90018.service.FirebaseService;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private Context context;
    private List<TotalPlan> tripList;
    private OnItemClickListener listener;
    private NavController navController;

    // 点击事件接口
    public interface OnItemClickListener {
        void onItemClick(TotalPlan totalPlan);
    }

    // 构造函数
    public TripAdapter(Context context, List<TotalPlan> tripList, OnItemClickListener listener,NavController navController) {
        this.context = context;
        this.tripList = tripList;
        this.listener = listener;
        this.navController = navController; // 接收 NavController
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
        TotalPlan plan = tripList.get(position); // 从已经填充好的列表中获取 TotalPlan

        // 设置 TotalPlan 数据到视图
        holder.textViewTitle.setText(plan.getName());
        holder.textViewDates.setText(plan.getStartDate() + " - " + plan.getEndDate());
        holder.textViewLocation.setText(plan.getCity());

        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(plan);
            }
        });
        // 设置本地图片资源
//        holder.imageViewThumbnail.setImageResource(trip.getThumbnailResId());
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
