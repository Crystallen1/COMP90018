package com.comp90018.comp90018;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.comp90018.comp90018.service.AuthenticationService;
import com.comp90018.comp90018.ui.login.LoginFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);  // 假设你的布局文件命名为 activity_login

//        // 检查 savedInstanceState 避免 Fragment 重叠
//        if (savedInstanceState == null) {
//            // 获取 FragmentManager 并加载 LoginFragment
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            fragmentManager.beginTransaction()
//                    .replace(R.id.fragment_container, new LoginFragment()) // fragment_container 是 activity_login.xml 中的容器ID
//                    .commit();
//        }
    }
}