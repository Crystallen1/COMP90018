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
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), recommendations, Toast.LENGTH_SHORT).show());
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
