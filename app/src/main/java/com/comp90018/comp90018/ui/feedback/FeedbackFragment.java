package com.comp90018.comp90018.ui.feedback;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.comp90018.comp90018.R;
import com.comp90018.comp90018.model.DayPlan;
import com.comp90018.comp90018.model.Journey;
import com.comp90018.comp90018.model.TotalPlan;
import com.comp90018.comp90018.service.FirebaseService;
import com.comp90018.comp90018.service.GPTService;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedbackFragment extends Fragment {

    private RatingBar ratingBarEnjoyment;
    private RadioGroup radioGroupTiredness;
    private EditText etOtherFeedback;
    private LinearLayout attractionContainer;
    private NavController navController;

    private TotalPlan totalPlan;
    private ArrayList<Journey> completedJourneys;
    private Integer stepCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);

        ratingBarEnjoyment = view.findViewById(R.id.ratingBarEnjoyment);
        radioGroupTiredness = view.findViewById(R.id.radioGroupTiredness);
        etOtherFeedback = view.findViewById(R.id.etOtherFeedback);
        attractionContainer = view.findViewById(R.id.attractionContainer);

        view.findViewById(R.id.btnFinish).setOnClickListener(v -> collectFeedback());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(requireView());

        // 从 PlanFragment 获取数据
        Bundle bundle = getArguments();
        if (bundle != null) {
            totalPlan = bundle.getParcelable("totalPlan");
            completedJourneys = bundle.getParcelableArrayList("completedJourneys");
            stepCount = bundle.getInt("stepCount", 0);

            // 在界面中动态显示当日已完成的 Journeys
            if (completedJourneys != null) {
                for (Journey journey : completedJourneys) {
                    CheckBox checkBox = new CheckBox(getContext());
                    checkBox.setText(journey.getName());
                    checkBox.setChecked(true);
                    checkBox.setEnabled(false); // 已完成的行程不可编辑
                    attractionContainer.addView(checkBox);
                }
            }
        }
    }

    private void collectFeedback() {
        int enjoyment = (int) ratingBarEnjoyment.getRating();
        int tirednessLevel = radioGroupTiredness.getCheckedRadioButtonId();
        String otherFeedback = etOtherFeedback.getText().toString().trim();

        // 创建并配置 TotalPlan 和目标地点 Map
        Map<String, Journey> targetViewPointMap = new HashMap<>();
        totalPlan.setTargetViewPoint(targetViewPointMap);

        GPTService.getInstance().getDayJourneyPlanByFeedback(totalPlan, enjoyment, tirednessLevel, otherFeedback, stepCount, new GPTService.GPTCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d("FeedbackFragment", result);
                List<Journey> journeySet = parseResultToJourneys(result, totalPlan.getTargetViewPoint());
                DayPlan dayPlan = new DayPlan();
                dayPlan.setJourneys(journeySet);

                // 设置下一个计划日期
                Calendar calendar = Calendar.getInstance();
                Date lastDate = totalPlan.getDayPlans().isEmpty() ? new Date() : totalPlan.getDayPlans().get(totalPlan.getDayPlans().size() - 1).getDate();
                calendar.setTime(lastDate);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                dayPlan.setDate(calendar.getTime());

                totalPlan.addDayPlans(dayPlan);
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                FirebaseService.getInstance().uploadTotalPlan(totalPlan, userId,
                        documentReference -> {
                            Log.d("PlanService", "DocumentSnapshot written with ID: " + documentReference.getId());
                            Toast.makeText(getContext(), "TotalPlan saved successfully!", Toast.LENGTH_SHORT).show();
                        },
                        e -> {
                            Log.w("PlanService", "Error adding document", e);
                            Toast.makeText(getContext(), "Failed to save TotalPlan", Toast.LENGTH_SHORT).show();
                        });

                Toast.makeText(getContext(), "Next day's plan: " + result, Toast.LENGTH_LONG).show();
                navController.navigate(R.id.action_feedback_to_plan);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getContext(), "Failed to get plan: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public List<Journey> parseResultToJourneys(String result, Map<String, Journey> journeyMap) {
        List<Journey> journeyList = new ArrayList<>();
        String[] ids = result.split("[,\\s]+");

        for (String idStr : ids) {
            if (idStr.matches("\\d+")) {
                Journey journey = journeyMap.get(idStr);
                if (journey != null) {
                    journeyList.add(journey);
                } else {
                    Log.e("PlanService", "No Journey found for ID: " + idStr);
                }
            } else {
                Log.e("PlanService", "Ignoring non-numeric ID: " + idStr);
            }
        }
        return journeyList;
    }
}
