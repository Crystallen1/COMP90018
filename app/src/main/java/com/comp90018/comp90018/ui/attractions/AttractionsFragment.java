package com.comp90018.comp90018.ui.attractions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.comp90018.comp90018.HomeViewModel;
import com.comp90018.comp90018.R;
import com.comp90018.comp90018.adapter.AttractionsAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AttractionsFragment extends Fragment {

    private RecyclerView recyclerViewAttractions;
    private NavController navController;
    private ImageButton btnBack;
    private HomeViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attractions, container, false);

        // 初始化视图
        btnBack = view.findViewById(R.id.btnBack);
        recyclerViewAttractions = view.findViewById(R.id.recyclerView_attractions);

        // 设置RecyclerView
        setupRecyclerView();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        navController= Navigation.findNavController(requireView());

        // 设置按钮点击事件
        btnBack.setOnClickListener(v -> navController.navigate(R.id.action_attraction_to_duration));

    }

    private void setupRecyclerView() {
        // 设置RecyclerView的布局管理器为 GridLayoutManager，列数为2
        recyclerViewAttractions.setLayoutManager(new GridLayoutManager(getContext(), 2));
//
        // 模拟景点数据
        List<String> attractionList = getAttractions();
//
//        // 设置适配器
//        AttractionsAdapter adapter = new AttractionsAdapter(attractionList);
//        recyclerViewAttractions.setAdapter(adapter);
    }

    // 模拟从数据源获取景点列表
    private List<String> getAttractions() {
        List<String> attractions = new ArrayList<>();
        attractions.add("Attraction 1");
        attractions.add("Attraction 2");
        attractions.add("Attraction 3");
        attractions.add("Attraction 4");
        // 添加更多景点
        return attractions;
    }
}