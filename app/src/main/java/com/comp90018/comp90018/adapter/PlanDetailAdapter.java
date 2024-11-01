//package com.comp90018.comp90018.adapter;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.comp90018.comp90018.R;
//import com.comp90018.comp90018.model.Journey;
//
//import java.util.List;
//
//public class PlanDetailAdapter extends RecyclerView.Adapter<PlanDetailAdapter.PlanDetailViewHolder> {
//
//    private List<Journey> journeys;
//
//    public PlanDetailAdapter(List<Journey> journeys) {
//        this.journeys = journeys;
//    }
//
//    @NonNull
//    @Override
//    public PlanDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_plan, parent, false);  // 使用 item_plan 作为布局文件
//        return new PlanDetailViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull PlanDetailViewHolder holder, int position) {
//        Journey journey = journeys.get(position);
//
//        // 设置地点名称和备注信息
//        holder.tvLocationTitle.setText(journey.getName());
//        holder.tvDescription.setText(journey.getNotes());
//
//        // 加载图片
//        Glide.with(holder.itemView.getContext())
//                .load(journey.getImageUrl())
//                .placeholder(R.drawable.ic_heart_outline)  // 替代图片
//                .into(holder.ivJourneyImage);
//
//        // 设置纬度和经度
//        holder.tvCoordinates.setText(
//                "Lat: " + journey.getLatitude() + ", Lon: " + journey.getLongitude()
//        );
//    }
//
//    @Override
//    public int getItemCount() {
//        return journeys.size();
//    }
//
//    // ViewHolder 内部类
//    static class PlanDetailViewHolder extends RecyclerView.ViewHolder {
//        TextView tvLocationTitle, tvDescription, tvCoordinates;
//        ImageView ivJourneyImage;
//
//        public PlanDetailViewHolder(@NonNull View itemView) {
//            super(itemView);
//            tvLocationTitle = itemView.findViewById(R.id.tvLocationTitle);
//            tvDescription = itemView.findViewById(R.id.tvDescription);
//            tvCoordinates = itemView.findViewById(R.id.tvCoordinates);
//            ivJourneyImage = itemView.findViewById(R.id.ivJourneyImage);
//        }
//    }
//}
