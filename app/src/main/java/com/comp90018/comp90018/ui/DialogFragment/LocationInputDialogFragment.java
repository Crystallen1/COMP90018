package com.comp90018.comp90018.ui.DialogFragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.comp90018.comp90018.R;
import com.comp90018.comp90018.service.LocationService;
import com.comp90018.comp90018.service.NavigationService;

import java.util.List;

public class LocationInputDialogFragment extends DialogFragment {

    private EditText editTextStartLocation, editTextEndLocation;
    private Button buttonConfirm;
    private NavigationService navigationService;
    private PopupWindow suggestionsPopup;

    // 接口用于将输入的数据传回 Fragment
    public interface OnLocationSetListener {
        void onLocationSet(String startLocation, String endLocation);
    }

    private OnLocationSetListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 加载布局
        View view = inflater.inflate(R.layout.dialog_location_input, container, false);
        navigationService = new NavigationService();

        // 初始化输入框和按钮
        editTextStartLocation = view.findViewById(R.id.editText_start_location);
        editTextEndLocation = view.findViewById(R.id.editText_end_location);
        buttonConfirm = view.findViewById(R.id.button_confirm);

        editTextStartLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 调用 getSuggestions 方法获取模糊搜索结果
                navigationService.getSuggestions(s.toString(), getContext(), suggestions -> {
                    displaySuggestions(suggestions, editTextStartLocation);  // 传入解析后的建议列表
                });
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        editTextEndLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                navigationService.getSuggestions(s.toString(), getContext(), suggestions -> {
                    displaySuggestions(suggestions, editTextEndLocation);  // 传入解析后的建议列表
                });
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 确认按钮点击事件
        buttonConfirm.setOnClickListener(v -> {
            String startLocation = editTextStartLocation.getText().toString().trim();
            String endLocation = editTextEndLocation.getText().toString().trim();

            // 调用接口方法将数据传回 Fragment
            if (listener != null) {
                listener.onLocationSet(startLocation, endLocation);
            }

            // 关闭对话框
            dismiss();
        });

        return view;
    }

    private void displaySuggestions(List<String> suggestions, EditText targetEditText) {
        // 您可以使用一个弹出框（例如 PopupWindow 或下拉菜单）来显示建议
        // 使用 `suggestions` 列表中的数据填充建议项
        // 当用户选择某个建议时，将该建议填入 targetEditText
        if (suggestionsPopup != null) {
            suggestionsPopup.dismiss();
        }

        // 创建一个 LinearLayout 容器，用于显示建议项
        LinearLayout suggestionsLayout = new LinearLayout(getContext());
        suggestionsLayout.setOrientation(LinearLayout.VERTICAL);

        // 遍历建议列表，创建 TextView 项
        for (String suggestion : suggestions) {
            View suggestionView = LayoutInflater.from(getContext()).inflate(R.layout.suggestion_item, null);
            TextView suggestionText = suggestionView.findViewById(R.id.suggestion_text);
            suggestionText.setText(suggestion);

            // 设置点击事件，当点击某个建议时，将其设置到 EditText 并关闭 PopupWindow
            suggestionText.setOnClickListener(v -> {
                targetEditText.setText(suggestion);
                suggestionsPopup.dismiss();
            });

            // 将建议项添加到布局中
            suggestionsLayout.addView(suggestionView);
        }

        // 创建并显示 PopupWindow
        suggestionsPopup = new PopupWindow(suggestionsLayout, targetEditText.getWidth(), WindowManager.LayoutParams.WRAP_CONTENT);
        suggestionsPopup.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        suggestionsPopup.setOutsideTouchable(true);
        suggestionsPopup.setFocusable(false);

        // 显示 PopupWindow 在 EditText 下方
        suggestionsPopup.showAsDropDown(targetEditText, 0, 10);
    }

    // 设置监听器
    public void setOnLocationSetListener(OnLocationSetListener listener) {
        this.listener = listener;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (suggestionsPopup != null && suggestionsPopup.isShowing()) {
            suggestionsPopup.dismiss();
        }
    }
}