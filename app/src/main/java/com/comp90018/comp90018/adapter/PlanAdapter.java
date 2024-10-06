package com.comp90018.comp90018.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.comp90018.comp90018.R;
import com.comp90018.comp90018.model.Plan;

import java.util.List;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.PlanViewHolder> {

    private Context context;
    private List<Plan> planList;

    public PlanAdapter(Context context, List<Plan> planList) {
        this.context = context;
        this.planList = planList;
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_plan, parent, false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        Plan plan = planList.get(position);
        holder.tvTime.setText(plan.getTime());
        holder.tvLocationTitle.setText(plan.getLocationTitle());
        holder.tvDescription.setText(plan.getDescription());

        // 设置删除按钮点击事件
        holder.ivDelete.setOnClickListener(v -> {
            Toast.makeText(context, "Deleted plan: " + plan.getLocationTitle(), Toast.LENGTH_SHORT).show();
            // 从列表中移除该计划并通知适配器
            planList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, planList.size());
        });
    }

    @Override
    public int getItemCount() {
        return planList.size();
    }

    public static class PlanViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime, tvLocationTitle, tvDescription;
        ImageView ivDelete;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvLocationTitle = itemView.findViewById(R.id.tvLocationTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}
