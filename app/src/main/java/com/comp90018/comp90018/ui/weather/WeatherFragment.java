// TODO: Add button and navigate to the desired fragment
// Example:
// Button weatherButton = view.findViewById(R.id.weather_button);
// weatherButton.setOnClickListener(v -> {
//     NavController navController = Navigation.findNavController(view);
//     navController.navigate(R.id.action_xxxFragment_to_weatherFragment);
// });

// fragment_xxx.xml
// example for the button:
// TODO: Add a button to trigger weather check in the XML layout file
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

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.comp90018.comp90018.R;

public class WeatherFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor temperatureSensor, humiditySensor, pressureSensor;

    private TextView temperatureTextView, humidityTextView, pressureTextView, actionTextView;

    // 各センサーの値を保持する変数
    private Float currentTemperature;
    private Float currentHumidity;
    private Float currentPressure;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        // Initialize text views
        temperatureTextView = view.findViewById(R.id.temperature_text);
        humidityTextView = view.findViewById(R.id.humidity_text);
        pressureTextView = view.findViewById(R.id.pressure_text);
        actionTextView = view.findViewById(R.id.action_text);

        // Get sensor manager
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);

        // Get sensors
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register sensor listeners
        if (temperatureSensor != null) {
            sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (humiditySensor != null) {
            sensorManager.registerListener(this, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (pressureSensor != null) {
            sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister sensor listeners
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            currentTemperature = event.values[0];
            temperatureTextView.setText("Temperature: " + currentTemperature + " °C");
        } else if (event.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) {
            currentHumidity = event.values[0];
            humidityTextView.setText("Humidity: " + currentHumidity + " %");
        } else if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
            currentPressure = event.values[0];
            pressureTextView.setText("Pressure: " + currentPressure + " hPa");
        }

        // 常に最新のセンサー値に基づいて推奨アクションを更新
        updateRecommendations(currentTemperature, currentHumidity, currentPressure);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle accuracy changes if necessary
    }

    private void updateRecommendations(Float temperature, Float humidity, Float pressure) {
        StringBuilder recommendations = new StringBuilder();

        if (temperature != null && humidity != null && pressure != null) {
            // 温度、湿度、気圧に基づいたより実用的なアドバイス
            if (temperature > 35) {
                if (humidity > 80) {
                    recommendations.append("Extreme heat and humidity! Stay in air-conditioned environments, avoid strenuous activities, and drink plenty of water. Consider wearing loose, light-colored clothing.\n");
                } else if (pressure < 1000) {
                    recommendations.append("Extremely hot with low pressure! Avoid outdoor activities, stay hydrated, and rest frequently. If outdoors, wear sunscreen and a hat.\n");
                } else {
                    recommendations.append("It's extremely hot! Limit outdoor activities, wear light, breathable clothing, and drink plenty of water. Sunscreen is recommended.\n");
                }
            } else if (temperature > 30) {
                if (humidity > 80) {
                    recommendations.append("Hot and humid! It's advisable to stay cool, take frequent breaks, and drink water. If going outdoors, carry an umbrella in case of rain.\n");
                } else if (pressure < 1000) {
                    recommendations.append("Hot with low pressure! Be mindful of fatigue, stay hydrated, and rest often. Light clothing and frequent water breaks are recommended.\n");
                } else {
                    recommendations.append("It's hot! Wear light, cool clothing, and ensure you stay hydrated throughout the day. A sun hat and sunglasses might be helpful.\n");
                }
            } else if (temperature > 20) {
                if (humidity < 30) {
                    recommendations.append("Comfortable temperature but low humidity. Keep hydrated and moisturize your skin, especially if you're spending time outdoors.\n");
                } else if (pressure > 1020) {
                    recommendations.append("Nice weather with clear skies! It's a great day for outdoor activities. Don't forget sunscreen if you're staying out for long.\n");
                } else {
                    recommendations.append("Weather is pleasant. A good day for a walk or outdoor exercise. Make sure to stay hydrated.\n");
                }
            } else if (temperature > 10) {
                if (humidity > 85) {
                    recommendations.append("Chilly and humid. Rain is possible, so dress warmly and carry an umbrella just in case.\n");
                } else if (pressure > 1020) {
                    recommendations.append("Cool but clear weather. A light jacket should suffice. Enjoy outdoor activities, but bring layers for comfort.\n");
                } else {
                    recommendations.append("Chilly weather. A jacket is recommended, and take care not to overexert yourself.\n");
                }
            } else {
                if (humidity > 85) {
                    recommendations.append("Cold and humid. Bundle up to stay warm and dry, and be prepared for rain with an umbrella or waterproof gear.\n");
                } else if (pressure > 1020) {
                    recommendations.append("Cold but clear skies. It might be a great day for outdoor activities, but wear warm clothing and protect yourself from the cold.\n");
                } else {
                    recommendations.append("Cold and low pressure. You might feel tired, so take it easy today. Warm clothing and indoor activities are recommended.\n");
                }
            }
        } else {
            recommendations.append("Not enough data to provide recommendations.");
        }

        actionTextView.setText(recommendations.toString());
    }

}
