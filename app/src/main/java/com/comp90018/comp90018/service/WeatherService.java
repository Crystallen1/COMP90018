package com.comp90018.comp90018.service;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class WeatherService implements SensorEventListener {

    private final SensorManager sensorManager;
    private Sensor temperatureSensor, humiditySensor, pressureSensor;

    private Float currentTemperature;
    private Float currentHumidity;
    private Float currentPressure;

    private WeatherUpdateListener weatherUpdateListener;

    private Timer timer;
    private final Random random = new Random();

    // コンストラクタ
    public WeatherService(Context context, WeatherUpdateListener listener) {
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.weatherUpdateListener = listener;

        // センサーの取得
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
    }

    // センサーリスナーの登録
    public void startSensorUpdates() {
        if (temperatureSensor != null) {
            sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (humiditySensor != null) {
            sensorManager.registerListener(this, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (pressureSensor != null) {
            sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

//        timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                generateRandomSensorData();
//                Log.d("Weater","update");
//            }
//        }, 0, 1000);
    }

    private void generateRandomSensorData() {
        currentTemperature = 10 + random.nextFloat() * 30;
        currentHumidity = 20 + random.nextFloat() * 80;
        currentPressure = 950 + random.nextFloat() * 100;
        Log.d("Weater",currentTemperature.toString()+" "+currentHumidity.toString()+" "+currentPressure.toString());

        // Fragmentに通知
        if (weatherUpdateListener != null) {
            weatherUpdateListener.onWeatherDataUpdated(currentTemperature, currentHumidity, currentPressure);
        }
    }

    // センサーリスナーの解除
    public void stopSensorUpdates() {
        sensorManager.unregisterListener(this);
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            currentTemperature = event.values[0];
        } else if (event.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) {
            currentHumidity = event.values[0];
        } else if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
            currentPressure = event.values[0];
        }

        // 各センサーの値をリスナーを通してFragmentに通知
        if (currentTemperature != null && currentHumidity != null && currentPressure != null) {
            weatherUpdateListener.onWeatherDataUpdated(currentTemperature, currentHumidity, currentPressure);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 必要に応じて精度変更を処理
    }

    // 推奨アクションを生成
    public String generateRecommendations(Float temperature, Float humidity, Float pressure) {
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

        return recommendations.toString();
    }

    // WeatherData更新のリスナーインターフェース
    public interface WeatherUpdateListener {
        void onWeatherDataUpdated(Float temperature, Float humidity, Float pressure);
    }
}
