package com.comp90018.comp90018.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.comp90018.comp90018.TestMapActivity;
import com.comp90018.comp90018.service.AuthenticationService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.android.material.textfield.TextInputEditText;
import com.comp90018.comp90018.R;

public class LoginFragment extends Fragment {

    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private AuthenticationService authenticationService;
    private TextView tvSignUp;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // 初始化 AuthenticationService
        authenticationService = new AuthenticationService(getActivity());

        // 初始化视图
        etUsername = view.findViewById(R.id.etUsername);
        etPassword = view.findViewById(R.id.etPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        progressBar = view.findViewById(R.id.progressBar);
        tvSignUp = view.findViewById(R.id.tvSignUp);

        // 按钮点击事件
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (validateInput(username, password)) {
                    // 显示进度条
                    progressBar.setVisibility(View.VISIBLE);

                    // 调用 AuthenticationService 的登录方法
                    authenticationService.signIn(username, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    // 隐藏进度条
                                    progressBar.setVisibility(View.GONE);

                                    if (task.isSuccessful()) {
                                        // 登录成功的处理
                                        Toast.makeText(getActivity(), "Login successful!", Toast.LENGTH_SHORT).show();
                                        // 登录成功后跳转到 MainActivity
                                        Intent intent = new Intent(getActivity(), TestMapActivity.class);
                                        startActivity(intent);
                                        getActivity().finish(); // 结束当前 Activity
                                    } else {
                                        // 登录失败的处理
                                        Toast.makeText(getActivity(), "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        // 设置点击事件，跳转到 RegisterFragment
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToRegisterFragment();
            }
        });

        return view;
    }

    private void navigateToRegisterFragment() {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // 创建 RegisterFragment 实例
        RegisterFragment registerFragment = new RegisterFragment();

        // 替换当前 Fragment 并将其添加到返回栈
        fragmentTransaction.replace(R.id.fragment_container, registerFragment);
        fragmentTransaction.addToBackStack(null);  // 将 transaction 添加到返回栈
        fragmentTransaction.commit();
    }

    // 验证输入
    private boolean validateInput(String username, String password) {
        if (username.isEmpty()) {
            etUsername.setError("Username cannot be empty");
            return false;
        }
        if (password.isEmpty()) {
            etPassword.setError("Password cannot be empty");
            return false;
        }
        return true;
    }
}