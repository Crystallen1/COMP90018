package com.comp90018.comp90018.ui.DialogFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.comp90018.comp90018.R;

public class LocationInputDialogFragment extends DialogFragment {

    private EditText editTextStartLocation, editTextEndLocation;
    private Button buttonConfirm;

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

        // 初始化输入框和按钮
        editTextStartLocation = view.findViewById(R.id.editText_start_location);
        editTextEndLocation = view.findViewById(R.id.editText_end_location);
        buttonConfirm = view.findViewById(R.id.button_confirm);

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

    // 设置监听器
    public void setOnLocationSetListener(OnLocationSetListener listener) {
        this.listener = listener;
    }
}