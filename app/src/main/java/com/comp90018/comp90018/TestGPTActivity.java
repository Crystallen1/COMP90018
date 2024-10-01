package com.comp90018.comp90018;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.comp90018.comp90018.service.GPTService;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class TestGPTActivity extends AppCompatActivity {

    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_gpt);

        // 初始化UI元素
        TextView resultTextView = findViewById(R.id.resultTextView);

        // 示例图片URL和经纬度
        String imageUrl = "https://encrypted-tbn1.gstatic.com/licensed-image?q=tbn:ANd9GcRsyNIF0NHp6bGF_WhbezupLSBMKLhAos73EWFU4mvY5Njsqk9ulOmAQ7YG2Y7SXJW9g9a7WwtGrxdX4DBTqBMXDRHgYtZzRO_FJuJu8A";
        double latitude = 43.7230;
        double longitude = 10.3966;
        // 在后台执行网络请求
        executorService.execute(() -> {
            // 在后台线程中调用 getImageBasedJourneyIntroduction
            GPTService gptService = new GPTService();
            String result = gptService.getImageBasedJourneyIntroduction(imageUrl, latitude, longitude);

            // 使用Handler回到主线程更新UI
            mainHandler.post(() -> {
                // 更新TextView显示网络请求结果
                resultTextView.setText(result);
            });
        });
    }
}
