package com.comp90018.comp90018.ui.feedback;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.comp90018.comp90018.R;

import java.util.ArrayList;
import java.util.List;

public class FeedbackFragment extends Fragment {

    private RatingBar ratingBarEnjoyment;
    private RadioGroup radioGroupTiredness;
    private Spinner spinnerFavoriteAttractions;
    private EditText etOtherFeedback;
    private Button btnFinish;
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 加载布局文件
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);

        // 初始化控件
        ratingBarEnjoyment = view.findViewById(R.id.ratingBarEnjoyment);
        radioGroupTiredness = view.findViewById(R.id.radioGroupTiredness);
        spinnerFavoriteAttractions = view.findViewById(R.id.spinnerFavoriteAttractions);
        etOtherFeedback = view.findViewById(R.id.etOtherFeedback);
        btnFinish = view.findViewById(R.id.btnFinish);

        // 初始化返回按钮
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        // 模拟从网络或数据库获取动态Attraction列表
        List<String> attractionList = getAttractionsFromDataSource();

        // 动态设置Spinner内容
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, attractionList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFavoriteAttractions.setAdapter(adapter);

        // 设置Finish按钮点击事件
        btnFinish.setOnClickListener(v -> collectFeedback());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(requireView());

    }

    // 模拟从数据源获取Attraction列表
    private List<String> getAttractionsFromDataSource() {
        List<String> attractions = new ArrayList<>();
        attractions.add("Attraction 1");
        attractions.add("Attraction 2");
        attractions.add("Attraction 3");
        return attractions;
    }

    // 收集用户反馈
    private void collectFeedback() {
        String enjoyment = String.valueOf(ratingBarEnjoyment.getRating());
        int tirednessLevel = radioGroupTiredness.getCheckedRadioButtonId();
        String favoriteAttraction = spinnerFavoriteAttractions.getSelectedItem().toString();
        String otherFeedback = etOtherFeedback.getText().toString().trim();

        if (!otherFeedback.isEmpty()) {
            Toast.makeText(getContext(), "Feedback Collected", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Please fill in the feedback", Toast.LENGTH_SHORT).show();
        }
    }
}