package com.comp90018.comp90018;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class StepCounterActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private TextView stepCountTextView;
    private boolean isCounterSensorPresent;
    private int stepCount = 0;
    private Handler handler = new Handler();  // 用于模拟数据
    private Runnable stepSimulator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("StepSimulator", "onCreate called, starting step simulator"); // 添加日志
        setContentView(R.layout.activity_step_counter);

        // 初始化TextView
        stepCountTextView = findViewById(R.id.step_count_text);

        // 初始化传感器管理器
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // 获取步数传感器
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isCounterSensorPresent = true;
        } else {
            stepCountTextView.setText("Step Counter Sensor Not Available!");
            isCounterSensorPresent = false;
        }

        // 启动模拟器
        startStepSimulator();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // 正常的传感器调用 - 已注释掉
        /*
        if (event.sensor == stepCounterSensor) {
            // 更新步数
            stepCount = (int) event.values[0];
            stepCountTextView.setText("step count: " + stepCount);
        }
        */
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 可选：处理传感器精度变化
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isCounterSensorPresent) {

            // 注册传感器监听器 - 已注释掉
            // sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isCounterSensorPresent) {
            // 注销传感器监听器 - 已注释掉
            // sensorManager.unregisterListener(this);
        }
    }

    // 模拟步数增长的函数
    private void startStepSimulator() {
        Log.d("StepSimulator", "Starting step simulator..."); // make sure get into this one
        stepSimulator = new Runnable() {
            @Override
            public void run() {
                // 模拟步数增加
                stepCount++;
                Log.d("StepSimulator", "Simulated step count: " + stepCount);// is it increasing?
                stepCountTextView.setText("step count: " + stepCount);
                // 每隔1秒更新一次步数
                handler.postDelayed(this, 5000);
            }
        };
        handler.post(stepSimulator); // 开始模拟步数
    }

    // 停止模拟步数
    private void stopStepSimulator() {
        handler.removeCallbacks(stepSimulator);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 停止模拟器
        stopStepSimulator();
    }
}
