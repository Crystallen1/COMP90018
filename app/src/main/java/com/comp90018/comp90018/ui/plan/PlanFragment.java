package com.comp90018.comp90018.ui.plan;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.comp90018.comp90018.R;
import com.comp90018.comp90018.adapter.DayPlanAdapter;
import com.comp90018.comp90018.model.DayPlan;
import com.comp90018.comp90018.model.Journey;
import com.comp90018.comp90018.model.TotalPlan;

import java.util.ArrayList;
import java.util.List;

public class PlanFragment extends Fragment {

    private static final String TAG = "PlanFragment";

    private RecyclerView recyclerView;
    private DayPlanAdapter dayPlanAdapter;
    private List<DayPlan> dayPlans = new ArrayList<>();
    private Button btnEndTrip;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plan, container, false);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        recyclerView = view.findViewById(R.id.recyclerView);
        btnEndTrip = view.findViewById(R.id.btnEndTrip);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 获取传递的 TotalPlan
        if (getArguments() != null && getArguments().containsKey("totalPlan")) {
            TotalPlan totalPlan = getArguments().getParcelable("totalPlan");
            if (totalPlan != null) {
                Log.d(TAG, "Received TotalPlan: " + totalPlan.getName());
                tvTitle.setText(totalPlan.getName());
                dayPlans = totalPlan.getDayPlans();

                if (dayPlans != null && !dayPlans.isEmpty()) {
                    setupDayPlanAdapter(dayPlans);
                } else {
                    Log.e(TAG, "No DayPlans available.");
                    Toast.makeText(getContext(), "No DayPlans available", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "TotalPlan is null");
            }
        } else {
            Log.e(TAG, "No TotalPlan found in arguments");
        }

        // 绑定 End Trip 按钮
        btnEndTrip.setOnClickListener(v -> endTrip(v));

        return view;
    }

    private void setupDayPlanAdapter(List<DayPlan> dayPlans) {
        dayPlanAdapter = new DayPlanAdapter(dayPlans);
        recyclerView.setAdapter(dayPlanAdapter);
        Log.d(TAG, "DayPlanAdapter set with " + dayPlans.size() + " items.");
    }

    // End Trip 按钮的逻辑
    private void endTrip(View view) {
        if (areAllJourneysFinished()) {
            Toast.makeText(getContext(), "Trip ended. Redirecting to feedback.", Toast.LENGTH_SHORT).show();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("completedJourneys", getCompletedJourneys());
            //TODO: 带更多的数据到feedback去
            Navigation.findNavController(view).navigate(R.id.action_plan_to_feedback, bundle);
        } else {
            Toast.makeText(getContext(), "Please finish all journeys before ending the trip.", Toast.LENGTH_SHORT).show();
        }
    }

    // 检查是否所有 Journey 都已完成
    private boolean areAllJourneysFinished() {
        for (DayPlan dayPlan : dayPlans) {
            for (Journey journey : dayPlan.getJourneys()) {
                if (!journey.isFinished()) {
                    return false;
                }
            }
        }
        return true;
    }

    // 获取已完成的 Journey 列表
    private ArrayList<Journey> getCompletedJourneys() {
        ArrayList<Journey> completedJourneys = new ArrayList<>();
        for (DayPlan dayPlan : dayPlans) {
            for (Journey journey : dayPlan.getJourneys()) {
                if (journey.isFinished()) {
                    completedJourneys.add(journey);
                }
            }
        }
        return completedJourneys;
    }
}
