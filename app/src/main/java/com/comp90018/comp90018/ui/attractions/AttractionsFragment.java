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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.comp90018.comp90018.HomeViewModel;
import com.comp90018.comp90018.adapter.AttractionsAdapter;
import com.comp90018.comp90018.model.DayPlan;
import com.comp90018.comp90018.model.Journey;
import com.comp90018.comp90018.model.TotalPlan;
import com.comp90018.comp90018.service.FirebaseService;
import com.comp90018.comp90018.service.GPTService;
import com.comp90018.comp90018.service.ViewpointService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.comp90018.comp90018.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;

public class AttractionsFragment extends Fragment {

    private static final String TAG = "AttractionsFragment";
    private RecyclerView recyclerViewAttractions;
    private AttractionsAdapter attractionsAdapter;
    private List<Journey> journeyList;
    private ViewpointService viewpointService;
    private MaterialButton backButton;
    private MaterialButton nextButton;
    private HomeViewModel viewModel;
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the Fragment layout
        View view = inflater.inflate(R.layout.fragment_attractions, container, false);
        Log.d(TAG, "onCreateView: Fragment视图已创建。");
        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        viewModel.getJourneys().observe(getViewLifecycleOwner(), journeys -> {
            if (journeys != null) {
                attractionsAdapter.updateJourneys(journeys);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TotalPlan totalPlan = viewModel.getTotalPlan().getValue();
        navController= Navigation.findNavController(requireView());

        backButton = view.findViewById(R.id.button_back);
        nextButton = view.findViewById(R.id.button_next);

        // 初始化 RecyclerView
        recyclerViewAttractions = view.findViewById(R.id.recyclerView_attractions);
        recyclerViewAttractions.setLayoutManager(new LinearLayoutManager(getContext()));
        Log.d(TAG, "onViewCreated: RecyclerView已初始化。");

        // 初始化数据列表和适配器
        journeyList = new ArrayList<>();
        attractionsAdapter = new AttractionsAdapter(getContext(), journeyList,navController);
        recyclerViewAttractions.setAdapter(attractionsAdapter);
        Log.d(TAG, "onViewCreated: Adapter已设置。");

        // 初始化服务
        viewpointService = new ViewpointService();

        loadJourneys(totalPlan.getCity());

        nextButton.setOnClickListener(v->navigationToPlan());
        backButton.setOnClickListener(v->navController.navigate(R.id.action_attraction_to_duration));
    }

    private void navigationToPlan() {
        TotalPlan totalPlan = viewModel.getTotalPlan().getValue();
        Set<Journey> journeys = viewModel.getWishListJourneys().getValue();
        Map<String,Journey> journeyMap = convertSetToMap(journeys);
        totalPlan.setTargetViewPoint(journeyMap);

        GPTService.getInstance().getDayJourneyPlan(totalPlan, new GPTService.GPTCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG,result);
                List<Journey> journeySet = parseResultToJourneys(result,totalPlan.getTargetViewPoint());
                DayPlan dayPlan  = new DayPlan();
                dayPlan.setJourneys(journeySet);
                dayPlan.setDate(viewModel.getTotalPlan().getValue().getStartDate());
                totalPlan.addDayPlans(dayPlan);
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // 当前用户ID

                FirebaseService.getInstance().uploadTotalPlan(totalPlan,userId,
                        documentReference -> {
                            // 处理 DocumentReference，例如输出新文档的 ID
                            Log.d("PlanService", "DocumentSnapshot written with ID: " + documentReference.getId());
                            Toast.makeText(getContext(), "TotalPlan saved successfully!", Toast.LENGTH_SHORT).show();
                        },
                        e -> {
                            Log.w("PlanService", "Error adding document", e);
                            Toast.makeText(getContext(), "Failed to save TotalPlan", Toast.LENGTH_SHORT).show();
                        });
            }

            @Override
            public void onFailure(String error) {

            }
        });

        viewModel.updateLiveData(totalPlan);
        navController.navigate(R.id.action_attraction_to_plan);
    }

    private void loadJourneys(String cityName) {
        Log.d(TAG, "loadJourneys: 开始加载城市 " + cityName + " 的数据。");
        Log.d(TAG, "loadJourneys: 开始加载城市 " + cityName + " 的数据。");
        viewpointService.getJourneysByCity(cityName, new ViewpointService.JourneyCallback() {
            @Override
            public void onSuccess(List<Journey> journeys) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // 更新 ViewModel 数据缓存
                        viewModel.setJourneys(journeys);
                        Log.d(TAG, "onSuccess: Journeys 加载完成，数量：" + journeys.size());
                    });
                }
            }


            @Override
            public void onFailure(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                        Log.e(TAG, "onFailure: 加载 Journeys 失败 - " + error);
                    });
                }
            }
        });
    }

    public Map<String, Journey> convertSetToMap(Set<Journey> journeys) {
        Map<String, Journey> journeyMap = new HashMap<>();
        int index = 1; // 用来作为Map的key值，从1开始编号
        for (Journey journey : journeys) {
            Log.d("AttractionFragment",journey.toString());
            journeyMap.put(String.valueOf(index++), journey); // 将 int 转换为 String 作为键
        }
        return journeyMap;
    }

    public List<Journey> parseResultToJourneys(String result, Map<String, Journey> journeyMap) {
        List<Journey> journeyList = new ArrayList<>();

        // 拆分 result 字符串，去掉空格并按逗号、回车或换行符分隔
        String[] ids = result.split("[,\\s]+");

        // 遍历解析的编号，检查是否为有效数字，获取对应的 Journey 对象并添加到列表中
        for (String idStr : ids) {
            // 使用正则表达式检查是否是数字
            if (idStr.matches("\\d+")) { // 仅匹配数字
                Journey journey = journeyMap.get(idStr); // 直接使用字符串从 map 中获取 Journey
                if (journey != null) {
                    journeyList.add(journey); // 添加到结果列表
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
