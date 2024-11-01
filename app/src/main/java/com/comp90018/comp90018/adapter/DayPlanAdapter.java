package com.comp90018.comp90018.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.comp90018.comp90018.R;
import com.comp90018.comp90018.model.DayPlan;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class DayPlanAdapter extends RecyclerView.Adapter<DayPlanAdapter.DayPlanViewHolder> {

    private List<DayPlan> dayPlans;

    public DayPlanAdapter(List<DayPlan> dayPlans) {
        this.dayPlans = dayPlans;
    }

    @NonNull
    @Override
    public DayPlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_day_plan, parent, false);
        return new DayPlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayPlanViewHolder holder, int position) {
        DayPlan dayPlan = dayPlans.get(position);
        holder.tvDate.setText(dayPlan.getDate().toString());

        // 初始化 JourneyAdapter
        JourneyAdapter journeyAdapter = new JourneyAdapter(dayPlan.getJourneys());
        holder.recyclerViewJourneys.setAdapter(journeyAdapter);
    }

    @Override
    public int getItemCount() {
        return dayPlans.size();
    }

    static class DayPlanViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        RecyclerView recyclerViewJourneys;

        public DayPlanViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            recyclerViewJourneys = itemView.findViewById(R.id.recyclerViewJourneys);
            recyclerViewJourneys.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }
    }
}
