package com.comp90018.comp90018.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.comp90018.comp90018.R;
import com.comp90018.comp90018.model.Plan;

import java.util.ArrayList;
import java.util.List;
public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.PlanViewHolder> {

    private Context context;
    private List<Plan> planList;
    private NavController navController;

    public PlanAdapter(Context context, List<Plan> planList, NavController navController) {
        this.context = context;
        this.planList = planList;
        this.navController = navController;
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

        // 削除ボタンの処理
        holder.ivDelete.setOnClickListener(v -> {
            Toast.makeText(context, "Deleted plan: " + plan.getLocationTitle(), Toast.LENGTH_SHORT).show();
            planList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, planList.size());
        });

        // End Trip ボタンの処理
        holder.endTripButton.setText("End Trip for " + plan.getTime());
        holder.endTripButton.setOnClickListener(v -> {
            List<Plan> upcomingPlans = getUpcomingPlansFromDate(plan.getTime());
            ArrayList<String> todaysAttractions = new ArrayList<>();
            todaysAttractions.add(plan.getLocationTitle() + ": " + plan.getDescription());

            // `PlanFragment`でデータを渡すためのBundleを生成
            ArrayList<String> planDetails = new ArrayList<>();
            for (Plan upcomingPlan : upcomingPlans) {
                planDetails.add(upcomingPlan.getTime() + " - " + upcomingPlan.getLocationTitle() + ": " + upcomingPlan.getDescription());
            }

            Bundle bundle = new Bundle();
            bundle.putString("tripInfo", "Trip for " + plan.getTime());
            bundle.putString("todayDate", plan.getTime());
            bundle.putStringArrayList("todaysAttractions", todaysAttractions);  // その日のアトラクション
            bundle.putStringArrayList("upcomingPlans", planDetails); // Plan情報を渡す

            navController.navigate(R.id.action_plan_to_feedback, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return planList.size();
    }

    // 日付以降のPlanを取得するメソッド
    private List<Plan> getUpcomingPlansFromDate(String date) {
        List<Plan> upcomingPlans = new ArrayList<>();
        boolean collect = false;
        for (Plan plan : planList) {
            if (plan.getTime().equals(date)) {
                collect = true;
            }
            if (collect) {
                upcomingPlans.add(plan);
            }
        }
        return upcomingPlans;
    }

    public static class PlanViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime, tvLocationTitle, tvDescription;
        ImageView ivDelete;
        Button endTripButton;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvLocationTitle = itemView.findViewById(R.id.tvLocationTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            endTripButton = itemView.findViewById(R.id.endTripButton);
        }
    }
}