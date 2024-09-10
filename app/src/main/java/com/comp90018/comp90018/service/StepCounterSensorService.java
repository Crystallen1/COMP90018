package com.comp90018.comp90018.service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class StepCounterSensorService implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;

    private int stepCount = 0;

    public StepCounterSensorService(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    public void startStepCounting() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stopStepCounting() {
        sensorManager.unregisterListener(this, accelerometer);
        sensorManager.unregisterListener(this, gyroscope);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            // 处理加速度数据，用于步数检测
            if (isStepDetected(x, y, z)) {
                stepCount++;
            }
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            // 可以结合陀螺仪数据改善步数检测的准确性
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 处理传感器精度变化
    }

    private boolean isStepDetected(float x, float y, float z) {
        // 步数检测的简单算法，根据加速度数据变化判断是否走了一步
        return Math.sqrt(x * x + y * y + z * z) > 12;  // 示例阈值，可以调整
    }

    public int getStepCount() {
        return stepCount;
    }
}