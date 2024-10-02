package com.comp90018.comp90018.service;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthenticationService {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Context context;

    // 构造函数接收Context
    public AuthenticationService(Context context) {
        this.context = context;
    }

    // 注册方法
    public Task<AuthResult> createAccount(String email, String password) {
        return mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 注册成功，获取用户信息
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show();
                        } else {
                            // 如果注册失败，显示错误消息
                            Toast.makeText(context, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // 登录方法
    public Task<AuthResult> signIn(String email, String password) {
        return mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 登录成功，获取用户信息
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show();
                        } else {
                            // 如果登录失败，显示错误消息
                            Toast.makeText(context, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // 登出方法
    public void signOut() {
        mAuth.signOut();
        Toast.makeText(context, "Logged out successfully!", Toast.LENGTH_SHORT).show();
    }
}