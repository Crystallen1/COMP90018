package com.comp90018.comp90018.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.comp90018.comp90018.databinding.FragmentHomeBinding;
import com.google.android.material.button.MaterialButton;
import com.comp90018.comp90018.R;


public class HomeFragment extends Fragment {

    private MaterialButton createTripButton;
    private MaterialButton viewAllButton;
    private TextView myTripsTitle;
    private RecyclerView recyclerViewTrips;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 将fragment_my_trips.xml布局文件转换为View
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 初始化布局中的组件
        createTripButton = view.findViewById(R.id.button_create_trip);
        viewAllButton = view.findViewById(R.id.button_view_all);
        myTripsTitle = view.findViewById(R.id.text_my_trips_title);
        recyclerViewTrips = view.findViewById(R.id.recycler_view_trips);

        // 设置按钮点击事件
        createTripButton.setOnClickListener(v -> {
            // 在这里处理创建新行程的逻辑
            createNewTrip();
        });

        viewAllButton.setOnClickListener(v -> {
            // 在这里处理查看所有行程的逻辑
            viewAllTrips();
        });

        // 设置RecyclerView，例如加载数据并适配器
        setupRecyclerView();

        return view;
    }

    private void createNewTrip() {
        // 创建新行程的逻辑
        // 例如跳转到新的页面或显示一个弹窗
    }

    private void viewAllTrips() {
        // 查看所有行程的逻辑
        // 例如跳转到显示所有行程的页面
    }

    private void setupRecyclerView() {
        // 在这里设置RecyclerView，例如设置LayoutManager和Adapter
        // recyclerViewTrips.setLayoutManager(new LinearLayoutManager(getContext()));
        // recyclerViewTrips.setAdapter(new MyTripsAdapter());
    }
}