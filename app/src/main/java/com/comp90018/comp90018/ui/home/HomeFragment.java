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
        navController= Navigation.findNavController(requireView());

        // 设置点击事件
        buttonCreateTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Create Trip Clicked", Toast.LENGTH_SHORT).show();
                // TODO: 实现导航到 CreateTripFragment
                // 例如：
                navController.navigate(R.id.action_home_to_create_trip);
            }
        });

        buttonViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "View All Clicked", Toast.LENGTH_SHORT).show();
                // TODO: 实现导航到 ViewAllTripsFragment 或类似页面
                // 例如：
//                Navigation.findNavController(view).navigate(R.id.fragment_create_trip);
            }
        });
    }

    /**
     * 设置 RecyclerView
     */
    private void setupRecyclerView() {
        // 初始化旅行列表
        tripList = new ArrayList<>();

// 获取数据并设置适配器
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // 当前用户ID
        FirebaseService.getInstance().getAllTotalPlans(userId,
                totalPlans -> {
                    // 将获取到的 totalPlans 传递给适配器
                    tripAdapter = new TripAdapter(getContext(), totalPlans, new TripAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(TotalPlan totalPlan) {
                            // 处理点击事件
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("totalPlan", totalPlan);
                            Navigation.findNavController(getView()).navigate(R.id.action_home_to_plan, bundle);
                        }
                    }, navController);

                    // 设置布局管理器和适配器
                    recyclerViewTrips.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerViewTrips.setAdapter(tripAdapter);
                },
                e -> {
                    Log.w("PlanService", "Error getting documents", e);
                });
    }
}
