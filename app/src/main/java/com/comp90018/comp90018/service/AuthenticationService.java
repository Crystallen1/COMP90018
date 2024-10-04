package com.comp90018.comp90018.service;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class AuthenticationService {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Context context;
    private String verificationId;


    // 构造函数接收Context
    public AuthenticationService(Context context) {
        this.context = context;
    }

    // 注册方法
    public Task<AuthResult> createAccount(String email, String password, AuthCallback callback) {
        return mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 注册成功，获取用户信息
                            FirebaseUser user = mAuth.getCurrentUser();

                            // 发送邮箱验证
                            if (user != null) {
                                user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    callback.onSuccess(user);
                                                    Toast.makeText(context, "Registration successful! Verification email sent.", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    callback.onFailure("Failed to send verification email.");
                                                    Toast.makeText(context, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            // 如果注册失败，显示错误消息
                            Toast.makeText(context, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // 忘记密码 - 发送重置密码邮件
    public void sendPasswordResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // 邮件发送成功
                            Toast.makeText(context, "Password reset email sent.", Toast.LENGTH_SHORT).show();
                        } else {
                            // 邮件发送失败
                            Toast.makeText(context, "Failed to send password reset email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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

    // 发送验证码
    public void sendVerificationCode(String phoneNumber, Activity activity) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)         // 手机号
                        .setTimeout(60L, TimeUnit.SECONDS)   // 超时
                        .setActivity(activity)               // 当前的Activity
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                                // 自动验证成功 (直接登录)
                                signInWithPhoneAuthCredential(credential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                // 验证失败
                                Toast.makeText(context, "Verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId,
                                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                // 验证码发送成功
                                AuthenticationService.this.verificationId = verificationId;
                                Toast.makeText(context, "Verification code sent!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    // 验证验证码并登录
    public void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 登录成功
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(context, "Phone login successful!", Toast.LENGTH_SHORT).show();
                        } else {
                            // 登录失败
                            Toast.makeText(context, "Phone login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    // 输入验证方法
    public boolean validateInput(TextInputEditText emailField, TextInputEditText passwordField, TextInputEditText confirmPasswordField) {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String confirmPassword = confirmPasswordField.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailField.setError("Email cannot be empty");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Password cannot be empty");
            return false;
        }

        if (password.length() < 6) {
            passwordField.setError("Password must be at least 6 characters long");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordField.setError("Passwords do not match");
            return false;
        }

        return true;
    }

    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(String errorMessage);
    }
}