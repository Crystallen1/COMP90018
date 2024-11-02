package com.comp90018.comp90018;

import static androidx.core.content.ContentProviderCompat.requireContext;

import static java.security.AccessController.getContext;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.comp90018.comp90018.service.WeatherService;
import com.comp90018.comp90018.ui.home.HomeFragment;
import com.comp90018.comp90018.ui.login.LoginFragment;
import com.comp90018.comp90018.ui.map.MapFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

public class HomeActivity extends AppCompatActivity implements WeatherService.WeatherUpdateListener{

    private WeatherService weatherService;
    // 定义一个变量记录上次显示Toast的时间
    private long lastToastTime = 0;
    private static final long TOAST_INTERVAL = 200000; // 设置时间间隔为2秒

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weatherService = new WeatherService(getApplicationContext(), this);
        setContentView(R.layout.activity_main);
        weatherService.startSensorUpdates();

    }

    @Override
    public void onWeatherDataUpdated(Float temperature, Float humidity, Float pressure) {

        // 推奨アクションを更新
        String recommendations = weatherService.generateRecommendations(temperature, humidity, pressure);
        showToast(recommendations);
    }

    private void showToast(String message) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastToastTime >= TOAST_INTERVAL) {
            runOnUiThread(() -> Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show());
            lastToastTime = currentTime;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        weatherService.stopSensorUpdates();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        weatherService.startSensorUpdates();
    }
}
