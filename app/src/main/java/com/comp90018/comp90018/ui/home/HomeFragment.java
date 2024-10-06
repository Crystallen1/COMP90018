// app/src/main/java/com/comp90018/comp90018/ui/home/HomeFragment.java
package com.comp90018.comp90018.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
// 导入必要的类
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.comp90018.comp90018.R;
import com.comp90018.comp90018.adapter.TripAdapter;
import com.comp90018.comp90018.model.Trip;
import com.google.android.material.button.MaterialButton;

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

        // 设置点击事件
        buttonCreateTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Create Trip Clicked", Toast.LENGTH_SHORT).show();
                // TODO: 实现导航到 CreateTripFragment
                // 例如：
                // Navigation.findNavController(view).navigate(R.id.action_home_to_createTrip);
            }
        });

        buttonViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "View All Clicked", Toast.LENGTH_SHORT).show();
                // TODO: 实现导航到 ViewAllTripsFragment 或类似页面
                // 例如：
                // Navigation.findNavController(view).navigate(R.id.action_home_to_viewAllTrips);
            }
        });

        return root;
    }

    /**
     * 设置 RecyclerView
     */
    private void setupRecyclerView() {
        // 初始化旅行列表
        tripList = new ArrayList<>();

        // 添加静态数据
        tripList.add(new Trip(
                "Beach Holiday",
                "2024-06-01",
                "2024-06-10",
                "Miami Beach",
                "Enjoy the sunny beaches.",
                R.drawable.ic_calendar // 确保此 drawable 存在
        ));
        tripList.add(new Trip(
                "Mountain Trek",
                "2024-07-15",
                "2024-07-20",
                "Rocky Mountains",
                "Hike through scenic trails.",
                R.drawable.ic_calendar // 确保此 drawable 存在
        ));
        tripList.add(new Trip(
                "City Tour",
                "2024-08-05",
                "2024-08-12",
                "New York City",
                "Explore the vibrant city life.",
                R.drawable.ic_calendar // 确保此 drawable 存在
        ));

        // 初始化适配器
        tripAdapter = new TripAdapter(getContext(), tripList, new TripAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Trip trip) {
                // 处理点击事件，例如导航到详细信息页面
                Toast.makeText(getContext(), "Clicked: " + trip.getTitle(), Toast.LENGTH_SHORT).show();
                // TODO: 实现导航逻辑，例如使用 Navigation Component 进行导航
                // 例如：
                // Bundle bundle = new Bundle();
                // bundle.putParcelable("trip", trip); // 假设 Trip 实现了 Parcelable
                // Navigation.findNavController(getView()).navigate(R.id.action_home_to_tripDetails, bundle);
            }
        });

        // 设置布局管理器
        recyclerViewTrips.setLayoutManager(new LinearLayoutManager(getContext()));

        // 设置适配器
        recyclerViewTrips.setAdapter(tripAdapter);
    }
}
