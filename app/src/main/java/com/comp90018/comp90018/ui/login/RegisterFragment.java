package com.comp90018.comp90018.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.comp90018.comp90018.TestMapActivity;
import com.comp90018.comp90018.service.AuthenticationService;
import com.comp90018.comp90018.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public class RegisterFragment  extends Fragment {
    private TextInputEditText etUseremail;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;
    private Button btnSignUp;
    private ProgressBar progressBar;
    private AuthenticationService authenticationService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 加载布局
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // 初始化 AuthenticationService
        authenticationService = new AuthenticationService(getActivity());

        // 初始化视图
        etUseremail = view.findViewById(R.id.etUseremail);
        etPassword = view.findViewById(R.id.etPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        btnSignUp = view.findViewById(R.id.btnSignUp);
        progressBar = view.findViewById(R.id.progressBar);

        // 设置按钮点击事件
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etUseremail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (authenticationService.validateInput(etUseremail, etPassword, etConfirmPassword)) {
                    progressBar.setVisibility(View.VISIBLE);

                    // 调用 AuthenticationService 中的 createAccount 方法
                    authenticationService.createAccount(email, password, new AuthenticationService.AuthCallback() {
                                @Override
                                public void onSuccess(FirebaseUser user) {
                                    if (user != null) {
                                        user.sendEmailVerification()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(getActivity(), "Registration successful! Verification email sent.", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(getActivity(), "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                }

                                @Override
                                public void onFailure(String errorMessage) {
                                }
                            })
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);

                                    if (task.isSuccessful()) {
                                        // 注册成功的处理
                                        // 登录成功后跳转到 MainActivity
                                        Intent intent = new Intent(getActivity(), TestMapActivity.class);
                                        startActivity(intent);
                                        getActivity().finish();
                                    } else {
                                        // 注册失败的处理
                                        Toast.makeText(getActivity(), "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        return view;
    }
}