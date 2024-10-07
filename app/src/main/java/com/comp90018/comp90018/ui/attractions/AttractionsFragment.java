//package com.comp90018.comp90018.ui.attractions;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageButton;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.navigation.NavController;
//import androidx.navigation.Navigation;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.comp90018.comp90018.R;
//import com.comp90018.comp90018.adapter.AttractionsAdapter;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class AttractionsFragment extends Fragment {
//
//    private RecyclerView recyclerViewAttractions;
//    private NavController navController;
//    private ImageButton btnBack;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_attractions, container, false);
//
//        // 初始化视图
//        btnBack = view.findViewById(R.id.btnBack);
//        recyclerViewAttractions = view.findViewById(R.id.recyclerView_attractions);
//        //fabWishlist = view.findViewById(R.id.fab_wishlist);
//
//        // 设置RecyclerView
//        setupRecyclerView();
//
//        return view;
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        navController= Navigation.findNavController(requireView());
//
//        // 设置按钮点击事件
//        btnBack.setOnClickListener(v -> navController.navigate(R.id.action_attraction_to_duration));
//
//    }
//
//    private void setupRecyclerView() {
//        // 设置RecyclerView的布局管理器为 GridLayoutManager，列数为2
//        recyclerViewAttractions.setLayoutManager(new GridLayoutManager(getContext(), 2));
//
//        // 模拟景点数据
//        List<String> attractionList = getAttractions();
////
////        // 设置适配器
////        AttractionsAdapter adapter = new AttractionsAdapter(attractionList);
////        recyclerViewAttractions.setAdapter(adapter);
//    }
//
//    // 模拟从数据源获取景点列表
//    private List<String> getAttractions() {
//        List<String> attractions = new ArrayList<>();
//        attractions.add("Attraction 1");
//        attractions.add("Attraction 2");
//        attractions.add("Attraction 3");
//        attractions.add("Attraction 4");
//        // 添加更多景点
//        return attractions;
//    }
//}

// File: com/comp90018/comp90018/AttractionsFragment.java

package com.comp90018.comp90018.ui.attractions;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.comp90018.comp90018.adapter.AttractionsAdapter;
import com.comp90018.comp90018.model.Journey;
import com.comp90018.comp90018.service.ViewpointService;

import java.util.ArrayList;
import java.util.List;

import com.comp90018.comp90018.R;

public class AttractionsFragment extends Fragment {

    private static final String TAG = "AttractionsFragment";
    private RecyclerView recyclerViewAttractions;
    private AttractionsAdapter attractionsAdapter;
    private List<Journey> journeyList;
    private ViewpointService viewpointService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the Fragment layout
        View view = inflater.inflate(R.layout.fragment_attractions, container, false);
        Log.d(TAG, "onCreateView: Fragment视图已创建。");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 初始化 RecyclerView
        recyclerViewAttractions = view.findViewById(R.id.recyclerView_attractions);
        recyclerViewAttractions.setLayoutManager(new LinearLayoutManager(getContext()));
        Log.d(TAG, "onViewCreated: RecyclerView已初始化。");

        // 初始化数据列表和适配器
        journeyList = new ArrayList<>();
        attractionsAdapter = new AttractionsAdapter(getContext(), journeyList);
        recyclerViewAttractions.setAdapter(attractionsAdapter);
        Log.d(TAG, "onViewCreated: Adapter已设置。");

        // 初始化服务
        viewpointService = new ViewpointService();

        // 获取指定城市的 Journeys，例如 "Sydney"
        String cityName = "Melbourne";
        loadJourneys(cityName);
    }

    private void loadJourneys(String cityName) {
        Log.d(TAG, "loadJourneys: 开始加载城市 " + cityName + " 的数据。");
        viewpointService.getJourneysByCity(cityName, new ViewpointService.JourneyCallback() {
            @Override
            public void onSuccess(List<Journey> journeys) {
                // 更新适配器的数据
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        attractionsAdapter.updateJourneys(journeys);
                        Log.d(TAG, "onSuccess: Journeys 加载完成，数量：" + journeys.size());
                    });
                }
            }

            @Override
            public void onFailure(String error) {
                // 处理错误，例如显示 Toast
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                        Log.e(TAG, "onFailure: 加载 Journeys 失败 - " + error);
                    });
                }
            }
        });
    }
}
