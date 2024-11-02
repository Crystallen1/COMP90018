package com.comp90018.comp90018.ui.plan;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.comp90018.comp90018.R;
import com.comp90018.comp90018.adapter.DayPlanAdapter;
import com.comp90018.comp90018.model.DayPlan;
import com.comp90018.comp90018.model.Journey;
import com.comp90018.comp90018.model.TotalPlan;
import com.comp90018.comp90018.service.LocationService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class PlanFragment extends Fragment {

    private static final String TAG = "PlanFragment";
    private static final String SHARED_PREFS_NAME = "JourneyPrefs";
    private static final float DISTANCE_THRESHOLD = 100; // 距离阈值，单位米

    private RecyclerView recyclerView;
    private DayPlanAdapter dayPlanAdapter;
    private List<DayPlan> dayPlans = new ArrayList<>();
    private Button btnEndTrip;
    private ImageButton btnBack;
    private TotalPlan totalPlan;
    private LocationService locationService;
    private Set<String> shownDialogJourneys = new HashSet<>(); // 记录已显示对话框的 Journey

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plan, container, false);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        recyclerView = view.findViewById(R.id.recyclerView);
        btnEndTrip = view.findViewById(R.id.btnEndTrip);
        btnBack = view.findViewById(R.id.btnBack);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null && getArguments().containsKey("totalPlan")) {
            totalPlan = getArguments().getParcelable("totalPlan");
            if (totalPlan != null) {
                Log.d(TAG, "Received TotalPlan: " + totalPlan.getName());
                tvTitle.setText(totalPlan.getName());
                dayPlans = totalPlan.getDayPlans();

                if (dayPlans != null && !dayPlans.isEmpty()) {
                    loadJourneyCompletionStates();
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

        btnEndTrip.setOnClickListener(this::endTrip);
        btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        locationService = new LocationService(requireContext());

        return view;
    }

    private void setupDayPlanAdapter(List<DayPlan> dayPlans) {
        dayPlanAdapter = new DayPlanAdapter(dayPlans);
        recyclerView.setAdapter(dayPlanAdapter);
        Log.d(TAG, "DayPlanAdapter set with " + dayPlans.size() + " items.");
    }

    @Override
    public void onResume() {
        super.onResume();
        startLocationUpdates(); // 开始位置监听
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates(); // 停止位置监听
        saveAllJourneyCompletionStates(); // 在暂停时保存状态
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        saveAllJourneyCompletionStates(); // 销毁视图时保存状态
    }

    private void startLocationUpdates() {
        locationService.startLocationUpdates(new LocationService.LocationCallback() {
            @Override
            public void onLocationResult(Location userLocation) {
                Log.d(TAG, "Location update received: Lat=" + userLocation.getLatitude() + ", Lon=" + userLocation.getLongitude());
                checkNearbyJourneys(userLocation);
            }

            @Override
            public void onLocationError(String errorMsg) {
                Log.e(TAG, "Location error: " + errorMsg);
            }
        });
    }

    private void stopLocationUpdates() {
        locationService.stopLocationUpdates();
    }

    private void checkNearbyJourneys(Location userLocation) {
        for (DayPlan dayPlan : dayPlans) {
            for (Journey journey : dayPlan.getJourneys()) {
                if (!journey.isFinished() && !shownDialogJourneys.contains(journey.getId())) {
                    Location journeyLocation = new Location("gps");
                    journeyLocation.setLatitude(journey.getLatitude());
                    journeyLocation.setLongitude(journey.getLongitude());

                    float distance = userLocation.distanceTo(journeyLocation);
                    Log.d(TAG, "Distance to " + journey.getName() + ": " + distance + " meters.");
                    if (distance <= DISTANCE_THRESHOLD) {
                        showNearbyJourneyDialog(journey);
                        shownDialogJourneys.add(journey.getId());
                        return;
                    }
                }
            }
        }
    }

    private void showNearbyJourneyDialog(Journey journey) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Nearby Journey")
                .setMessage("You are close to " + journey.getName() + ". Would you like to mark it as visited?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    journey.setFinished(true);
                    saveJourneyCompletionState(journey);
                    Toast.makeText(getContext(), "Marked as visited", Toast.LENGTH_SHORT).show();
                    dayPlanAdapter.notifyDataSetChanged();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void endTrip(View view) {
        if (areAllJourneysFinished()) {
            proceedToFeedback(view);
        } else {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Incomplete Journeys")
                    .setMessage("Some journeys today are still incomplete. Would you like to mark them all as completed?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        markAllJourneysAsFinished();
                        proceedToFeedback(view);
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        }
    }

    private void markAllJourneysAsFinished() {
        for (DayPlan dayPlan : dayPlans) {
            for (Journey journey : dayPlan.getJourneys()) {
                journey.setFinished(true);
                saveJourneyCompletionState(journey);
            }
        }
        dayPlanAdapter.notifyDataSetChanged();
    }

    private void proceedToFeedback(View view) {
        Toast.makeText(getContext(), "Trip ended. Redirecting to feedback.", Toast.LENGTH_SHORT).show();

        int stepCount = getTodayStepCount();

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("completedJourneys", getCompletedJourneys());
        bundle.putParcelable("totalPlan", totalPlan);
        bundle.putInt("stepCount", stepCount);

        Navigation.findNavController(view).navigate(R.id.action_plan_to_feedback, bundle);
    }

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

    private int getTodayStepCount() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        String dateKey = "stepCount_" + new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Calendar.getInstance().getTime());
        return sharedPreferences.getInt(dateKey, 0);
    }

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

    private void saveJourneyCompletionState(Journey journey) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String uniqueKey = getUniqueKeyForJourney(journey);
        editor.putBoolean(uniqueKey, journey.isFinished());
        editor.apply();
    }

    private void loadJourneyCompletionStates() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        for (DayPlan dayPlan : dayPlans) {
            for (Journey journey : dayPlan.getJourneys()) {
                boolean isFinished = sharedPreferences.getBoolean(getUniqueKeyForJourney(journey), false);
                journey.setFinished(isFinished);
            }
        }
    }

    private void saveAllJourneyCompletionStates() {
        for (DayPlan dayPlan : dayPlans) {
            for (Journey journey : dayPlan.getJourneys()) {
                saveJourneyCompletionState(journey);
            }
        }
    }

    private String getUniqueKeyForJourney(Journey journey) {
        return journey.getId() + "_" + totalPlan.getName();
    }
}
