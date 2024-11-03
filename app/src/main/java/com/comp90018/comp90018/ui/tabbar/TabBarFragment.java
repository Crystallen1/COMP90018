package com.comp90018.comp90018.ui.tabbar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import com.comp90018.comp90018.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TabBarFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 加载布局文件
        View view = inflater.inflate(R.layout.fragment_tab_bar, container, false);

        // 获取 NavController
        NavController navController = Navigation.findNavController(requireActivity(), R.id.fragment_container);

        // 设置 BottomNavigationView
        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            // 使用 if-else 代替 switch-case
            if (item.getItemId() == R.id.nav_home) {
                navController.navigate(R.id.navigation_home);
            } else if (item.getItemId() == R.id.nav_map) {
                navController.navigate(R.id.navigation_map);
            } else if (item.getItemId() == R.id.nav_profile) {
                navController.navigate(R.id.navigation_user_profile);
            }
            return true;
        });
        return view;
    }
}