package com.comp90018.comp90018.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class StepCountService extends Service implements SensorEventListener {
    private static final String SHARED_PREFS_NAME = "StepCountPrefs";
    private SensorManager sensorManager;
    private int stepCount = 0;
    private long lastUpdate = 0;
    private float lastX, lastY, lastZ;
    private static final int SHAKE_THRESHOLD = 800;  // 根据设备调整阈值

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // 使用加速度传感器替代步数传感器
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d("StepCountService", "Using Accelerometer for step detection.");
        } else {
            Log.e("StepCountService", "Accelerometer sensor not available.");
        }

        // 加载当天的步数
        stepCount = getTodayStepCount();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("StepCountService", "StepCountService started.");
        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();

            // 每隔100毫秒检测一次步数（或根据需要调整时间间隔）
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                // 计算加速度变化
                float speed = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    stepCount++;
                    Log.d("StepCountService", "Step detected. Total steps: " + stepCount);

                    sendStepCountBroadcast(stepCount);

                    // 保存步数到 SharedPreferences 中
                    saveTodayStepCount(stepCount);
                }

                lastX = x;
                lastY = y;
                lastZ = z;
            }
        }
    }

    // 获取当前日期的键值，例如"stepCount_20241026"
    private String getCurrentDateKey() {
        return "stepCount_" + new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Calendar.getInstance().getTime());
    }

    // 获取今天的步数
    private int getTodayStepCount() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(getCurrentDateKey(), 0); // 默认步数为0
    }

    // 保存今天的步数
    private void saveTodayStepCount(int steps) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getCurrentDateKey(), steps); // 用动态日期键保存步数
        editor.apply();
        Log.d("StepCountService", "Step count saved to SharedPreferences: " + steps);
    }

    // 根据日期（例如"20241025"）获取特定日期的步数
    public int getStepCountByDate(String dateKey) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt("stepCount_" + dateKey, 0); // 默认返回0
    }

    // 获取最近几天的步数数据
    public Map<String, Integer> getRecentStepCounts(int days) {
        Map<String, Integer> recentSteps = new HashMap<>();
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < days; i++) {
            // 获取当天的步数
            String dateKey = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(calendar.getTime());
            int stepCount = getStepCountByDate(dateKey);
            recentSteps.put(dateKey, stepCount);

            // 日期回退一天
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        }
        return recentSteps;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 不需要处理精度变化
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
            Log.d("StepCountService", "Step Counter sensor unregistered.");
        }
    }

    private void sendStepCountBroadcast(int stepCount) {
        Intent intent = new Intent("com.comp90018.STEP_COUNT_UPDATED");
        intent.putExtra("stepCount", stepCount);
        sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
