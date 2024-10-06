package com.comp90018.comp90018.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.comp90018.comp90018.R;
import com.comp90018.comp90018.service.AuthenticationService;

public class ForgotPasswordFragment extends Fragment {

    private EditText etForgotEmail;
    private ImageButton btnBack;
    private Button btnSubmitForgotPassword;
    private ProgressBar progressBarForgotPassword;
    private TextView llBackToLogin;
    private AuthenticationService authService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forgetpassword, container, false);

        // 初始化UI组件
        btnBack = view.findViewById(R.id.btnBack);
        etForgotEmail = view.findViewById(R.id.etForgotEmail);
        btnSubmitForgotPassword = view.findViewById(R.id.btnSubmitForgotPassword);
        progressBarForgotPassword = view.findViewById(R.id.progressBarForgotPassword);
        llBackToLogin = view.findViewById(R.id.tvBackToLoginText);

        authService = new AuthenticationService(getActivity());


        // 设置返回登录点击事件
        llBackToLogin.setOnClickListener(v -> {
            // 处理返回登录的操作，例如导航到登录Fragment
            navigateToLoginFragment();
        });
        btnBack.setOnClickListener(v->{
            navigateToLoginFragment();
        });

        // 设置提交按钮点击事件
        btnSubmitForgotPassword.setOnClickListener(v -> {
            // 获取输入的电子邮件
            String email = etForgotEmail.getText().toString().trim();
            if (email.isEmpty()) {
                etForgotEmail.setError("Please enter your email");
                return;
            }

            // 显示进度条
            progressBarForgotPassword.setVisibility(View.VISIBLE);

            handleForgotPassword(email);
        });

        return view;
    }

    private void navigateToLoginFragment() {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // 创建 RegisterFragment 实例
        LoginFragment loginFragment = new LoginFragment();

        // 替换当前 Fragment 并将其添加到返回栈
        fragmentTransaction.replace(R.id.fragment_container, loginFragment);
        fragmentTransaction.addToBackStack(null);  // 将 transaction 添加到返回栈
        fragmentTransaction.commit();
    }

    // 使用AuthenticationService处理密码重置
    private void handleForgotPassword(String email) {
        authService.sendPasswordResetEmail(email);

        // 隐藏进度条
        progressBarForgotPassword.setVisibility(View.GONE);
    }

}
