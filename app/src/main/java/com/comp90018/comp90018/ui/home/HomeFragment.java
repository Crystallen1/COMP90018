// app/src/main/java/com/comp90018/comp90018/ui/home/HomeFragment.java
package com.comp90018.comp90018.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
// 导入必要的类
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.comp90018.comp90018.R;
import com.comp90018.comp90018.adapter.TripAdapter;
import com.comp90018.comp90018.model.DayPlan;
import com.comp90018.comp90018.model.TotalPlan;
import com.comp90018.comp90018.model.Trip;
import com.comp90018.comp90018.service.FirebaseService;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.navigation.Navigation;

public class HomeFragment extends Fragment {

    private MaterialButton buttonCreateTrip;
    private MaterialButton buttonViewAll;
    private RecyclerView recyclerViewTrips;

    private TripAdapter tripAdapter;
    private List<Trip> tripList;
    private NavController navController;

    private boolean isFullPlanLoaded = false; // 追踪按钮的状态

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // 填充布局
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // 初始化视图
        buttonCreateTrip = root.findViewById(R.id.button_create_trip);
        buttonViewAll = root.findViewById(R.id.button_view_all);
        recyclerViewTrips = root.findViewById(R.id.recycler_view_trips);

        // 设置 RecyclerView
        setupRecyclerView();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(requireView());

        // 设置创建按钮的点击事件
        buttonCreateTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Create Trip Clicked", Toast.LENGTH_SHORT).show();
                navController.navigate(R.id.action_home_to_create_trip);
            }
        });

        // 设置查看全部按钮的点击事件
        buttonViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFullPlanLoaded) {
                    // 加载完整计划
                    Toast.makeText(getContext(), "Loading full plan...", Toast.LENGTH_SHORT).show();
                    loadFullPlan();
                    buttonViewAll.setText("Show Ongoing Plans"); // 更新按钮文本
                } else {
                    // 加载正在进行的计划
                    Toast.makeText(getContext(), "Loading ongoing plans...", Toast.LENGTH_SHORT).show();
                    setupRecyclerView(); // 调用初始设置方法，加载进行中的计划
                    buttonViewAll.setText("View All Plans"); // 恢复按钮文本
                }
                isFullPlanLoaded = !isFullPlanLoaded; // 切换状态
            }
        });
    }

    /**
     * 设置 RecyclerView，显示进行中的计划
     */
    private void setupRecyclerView() {
        tripList = new ArrayList<>();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseService.getInstance().getOngoingPlans(userId,
                totalPlans -> {
                    tripAdapter = new TripAdapter(getContext(), totalPlans, new TripAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(TotalPlan totalPlan) {
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("totalPlan", totalPlan);
                            navController.navigate(R.id.action_home_to_plan, bundle);
                        }
                    }, navController);

                    recyclerViewTrips.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerViewTrips.setAdapter(tripAdapter);
                },
                e -> Log.w("PlanService", "Error getting documents", e));
    }

    /**
     * 加载全部计划
     */
    private void loadFullPlan() {
        tripList = new ArrayList<>();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseService.getInstance().getAllTotalPlans(userId,
                totalPlans -> {
                    tripAdapter = new TripAdapter(getContext(), totalPlans, new TripAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(TotalPlan totalPlan) {
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("totalPlan", totalPlan);
                            navController.navigate(R.id.action_home_to_plan, bundle);
                        }
                    }, navController);

                    recyclerViewTrips.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerViewTrips.setAdapter(tripAdapter);
                },
                e -> Log.w("PlanService", "Error getting documents", e));
    }
}
