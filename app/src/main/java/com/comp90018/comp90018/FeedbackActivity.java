package com.comp90018.comp90018;


import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class FeedbackActivity extends AppCompatActivity {

    private RatingBar ratingBarEnjoyment;
    private RadioGroup radioGroupTiredness;
    private Spinner spinnerFavoriteAttractions;
    private EditText etOtherFeedback;
    private Button btnFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        // 初始化控件
        ratingBarEnjoyment = findViewById(R.id.ratingBarEnjoyment);
        radioGroupTiredness = findViewById(R.id.radioGroupTiredness);
        spinnerFavoriteAttractions = findViewById(R.id.spinnerFavoriteAttractions);
        etOtherFeedback = findViewById(R.id.etOtherFeedback);
        btnFinish = findViewById(R.id.btnFinish);

        // 初始化返回按钮
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // 模拟从网络或数据库获取动态Attraction列表
        List<String> attractionList = getAttractionsFromDataSource();

        // 动态设置Spinner内容
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, attractionList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFavoriteAttractions.setAdapter(adapter);

        // 设置Finish按钮点击事件
        btnFinish.setOnClickListener(v -> collectFeedback());
    }

    // 模拟从数据源获取Attraction列表（可替换为实际的网络请求或数据库查询）
    private List<String> getAttractionsFromDataSource() {
        List<String> attractions = new ArrayList<>();
        attractions.add("Attraction 1");
        attractions.add("Attraction 2");
        attractions.add("Attraction 3");
        // 你可以动态添加或替换这里的Attraction数据
        return attractions;
    }

    // 收集用户反馈
    private void collectFeedback() {
        // 获取享受度评分

    }
}
