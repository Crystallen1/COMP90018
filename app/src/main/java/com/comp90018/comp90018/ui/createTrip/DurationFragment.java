package com.comp90018.comp90018.ui.createTrip;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.comp90018.comp90018.HomeViewModel;
import com.comp90018.comp90018.R;
import com.comp90018.comp90018.model.TotalPlan;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DurationFragment extends Fragment {

    private TextInputEditText editTextStartDate, editTextEndDate;
    private Calendar calendarStart, calendarEnd;
    private NavController navController;
    private ImageButton btnBack;
    private MaterialButton buttonBack;
    private MaterialButton buttonNext;
    private HomeViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip_duration, container, false);

        // 初始化视图
        btnBack = view.findViewById(R.id.btnBack);
        buttonBack = view.findViewById(R.id.button_back);
        buttonNext = view.findViewById(R.id.button_next);
        editTextStartDate = view.findViewById(R.id.editText_start_date);
        editTextEndDate = view.findViewById(R.id.editText_end_date);

        // 初始化日期
        calendarStart = Calendar.getInstance();
        calendarEnd = Calendar.getInstance();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        navController= Navigation.findNavController(requireView());

        // 设置返回按钮点击事件
        btnBack.setOnClickListener(v -> {
            navController.navigate(R.id.action_duration_to_create_trip);
            // 返回上一个页面或关闭当前Fragment
        });

        // 设置日期选择器
        setupDatePicker();

        // 设置Back按钮点击事件
        buttonBack.setOnClickListener(v -> {
            // 返回上一个Fragment或其他逻辑
            navController.navigate(R.id.action_duration_to_create_trip);
        });

        // 设置Next按钮点击事件
        buttonNext.setOnClickListener(v -> {
            // 验证输入日期或执行其他逻辑
            if (validateDates()) {
                navigationToAttraction();
                Toast.makeText(getContext(), "Next button clicked", Toast.LENGTH_SHORT).show();
                // 执行下一步操作，或者导航到下一步的Fragment
            } else {
                Toast.makeText(getContext(), "Please enter valid dates", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigationToAttraction(){
        TotalPlan totalPlan = viewModel.getTotalPlan().getValue();
        totalPlan.setStartDate(calendarStart.getTime());
        totalPlan.setEndDate(calendarEnd.getTime());
        long durationInMillis = calendarEnd.getTimeInMillis() - calendarStart.getTimeInMillis();
        totalPlan.setDuration(Math.toIntExact(durationInMillis / (1000 * 60 * 60 * 24)));
        viewModel.updateLiveData(totalPlan);
        navController.navigate(R.id.action_duration_to_attraction);
    }

    private void setupDatePicker() {
        // 开始日期选择器
        editTextStartDate.setOnClickListener(v -> {
            new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
                calendarStart.set(year, month, dayOfMonth);
                updateLabel(editTextStartDate, calendarStart);
            }, calendarStart.get(Calendar.YEAR), calendarStart.get(Calendar.MONTH),
                    calendarStart.get(Calendar.DAY_OF_MONTH)).show();
        });

        // 结束日期选择器
        editTextEndDate.setOnClickListener(v -> {
            new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
                calendarEnd.set(year, month, dayOfMonth);
                updateLabel(editTextEndDate, calendarEnd);
            }, calendarEnd.get(Calendar.YEAR), calendarEnd.get(Calendar.MONTH),
                    calendarEnd.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    // 更新日期显示格式
    private void updateLabel(TextInputEditText editText, Calendar calendar) {
        String format = "yyyy-MM-dd"; // 设置日期格式
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        editText.setText(sdf.format(calendar.getTime()));
    }

    // 验证日期是否有效
    private boolean validateDates() {
        if (editTextStartDate.getText().toString().isEmpty() || editTextEndDate.getText().toString().isEmpty()) {
            return false;
        }
        return !calendarStart.after(calendarEnd); // 确保开始日期不晚于结束日期
    }
}