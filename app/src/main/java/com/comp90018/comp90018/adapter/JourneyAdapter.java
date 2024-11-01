package com.comp90018.comp90018.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.comp90018.comp90018.R;
import com.comp90018.comp90018.model.Journey;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JourneyAdapter extends RecyclerView.Adapter<JourneyAdapter.JourneyViewHolder> {

    private final List<Journey> journeys;

    public JourneyAdapter(List<Journey> journeys) {
        this.journeys = journeys;
    }

    @NonNull
    @Override
    public JourneyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 使用 ViewGroup 的 context 创建 LayoutInflater
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_journey, parent, false);
        return new JourneyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JourneyViewHolder holder, int position) {
        Journey journey = journeys.get(position);

        holder.tvLocationTitle.setText(journey.getName());
        holder.tvDescription.setText(journey.getNotes());

        // 使用 Glide 加载图片，使用 itemView 的 Context
        Glide.with(holder.itemView.getContext())
                .load(journey.getImageUrl())
                .placeholder(R.drawable.ic_heart_outline)
                .into(holder.ivJourneyImage);

        // 设置按钮状态
        holder.btnFinish.setText(journey.isFinished() ? "Finished" : "Mark as Finished");

        // 完成按钮点击事件
        holder.btnFinish.setOnClickListener(v -> {
            boolean newState = !journey.isFinished();
            journey.setFinished(newState);

            // 更新按钮文本
            holder.btnFinish.setText(newState ? "Finished" : "Mark as Finished");

            // 显示 Toast 提示
            Toast.makeText(holder.itemView.getContext(), newState ? "Journey marked as finished!" : "Journey marked as unfinished.", Toast.LENGTH_SHORT).show();

            // 持久化状态
            saveFinishedState(journey.getId(), newState, holder.itemView.getContext());
        });
    }

    @Override
    public int getItemCount() {
        return journeys.size();
    }

    /**
     * ViewHolder 内部类
     */
    static class JourneyViewHolder extends RecyclerView.ViewHolder {
        TextView tvLocationTitle, tvDescription;
        ImageView ivJourneyImage;
        Button btnFinish;

        public JourneyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLocationTitle = itemView.findViewById(R.id.tvLocationTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivJourneyImage = itemView.findViewById(R.id.ivJourneyImage);
            btnFinish = itemView.findViewById(R.id.btnFinish);
        }
    }

    /**
     * 将 Journey 的完成状态保存到 SharedPreferences
     */
    private void saveFinishedState(String journeyId, boolean isFinished, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("journeys_prefs", Context.MODE_PRIVATE);
        Set<String> finishedJourneys = sharedPreferences.getStringSet("finished_journeys", new HashSet<>());

        if (isFinished) {
            finishedJourneys.add(journeyId);  // 标记为完成
        } else {
            finishedJourneys.remove(journeyId);  // 取消完成
        }

        // 保存到 SharedPreferences
        sharedPreferences.edit().putStringSet("finished_journeys", finishedJourneys).apply();
    }

    /**
     * 从 SharedPreferences 恢复已完成的 Journey 状态
     */
    public void restoreFinishedStates(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("journeys_prefs", Context.MODE_PRIVATE);
        Set<String> finishedJourneys = sharedPreferences.getStringSet("finished_journeys", new HashSet<>());

        for (Journey journey : journeys) {
            if (finishedJourneys.contains(journey.getId())) {
                journey.setFinished(true);
            }
        }
    }
}
