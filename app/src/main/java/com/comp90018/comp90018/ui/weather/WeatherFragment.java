// Example:
// Button weatherButton = view.findViewById(R.id.weather_button);
// weatherButton.setOnClickListener(v -> {
//     NavController navController = Navigation.findNavController(view);
//     navController.navigate(R.id.action_xxxFragment_to_weatherFragment);
// });

// fragment_xxx.xml
// example for the button:
// Example (XML):
// <Button
//     android:id="@+id/weather_button"
//     android:layout_width="wrap_content"
//     android:layout_height="wrap_content"
//     android:text="Check Weather"
//     android:layout_margin="16dp"
//     app:layout_constraintTop_toTopOf="parent"
//     app:layout_constraintStart_toStartOf="parent"
//     app:layout_constraintEnd_toEndOf="parent" />
package com.comp90018.comp90018.ui.weather;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.comp90018.comp90018.R;
import com.comp90018.comp90018.service.WeatherService;

public class WeatherFragment extends Fragment implements WeatherService.WeatherUpdateListener {

    private WeatherService weatherService;

    private TextView temperatureTextView, humidityTextView, pressureTextView, actionTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        // Initialize text views
        temperatureTextView = view.findViewById(R.id.temperature_text);
        humidityTextView = view.findViewById(R.id.humidity_text);
        pressureTextView = view.findViewById(R.id.pressure_text);
        actionTextView = view.findViewById(R.id.action_text);

        // WeatherServiceの初期化
        weatherService = new WeatherService(requireContext(), this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // WeatherServiceでセンサーの監視を開始
        weatherService.startSensorUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();
        // センサーの監視を停止
        weatherService.stopSensorUpdates();
    }

    // センサーから更新された天気データを受け取る
    @Override
    public void onWeatherDataUpdated(Float temperature, Float humidity, Float pressure) {
        temperatureTextView.setText("Temperature: " + temperature + " °C");
        humidityTextView.setText("Humidity: " + humidity + " %");
        pressureTextView.setText("Pressure: " + pressure + " hPa");

        // 推奨アクションを更新
        String recommendations = weatherService.generateRecommendations(temperature, humidity, pressure);
        actionTextView.setText(recommendations);
    }
}
