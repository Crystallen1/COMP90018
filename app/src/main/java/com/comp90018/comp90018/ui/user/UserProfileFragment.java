package com.comp90018.comp90018.ui.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.comp90018.comp90018.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

public class UserProfileFragment extends Fragment {

    private TextView userIdTextView, emailTextView, displayNameTextView,displayLastSignInTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化视图
        userIdTextView = view.findViewById(R.id.userIdTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        displayNameTextView = view.findViewById(R.id.displayNameTextView);
        displayLastSignInTextView = view.findViewById(R.id.displaylastSignInDateTextView);

        // 获取当前用户信息
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // 设置用户信息到TextView
            userIdTextView.setText("User ID: " + user.getUid());
            emailTextView.setText("Email: " + user.getEmail());
            displayNameTextView.setText("Display Name: " + user.getDisplayName()!=null?user.getDisplayName():"");
            displayLastSignInTextView.setText("Last Sign In Time: " + new Date(user.getMetadata().getLastSignInTimestamp()).toString());
        } else {
            // 用户未登录的情况
            userIdTextView.setText("User not logged in");
            emailTextView.setText("No email available");
            displayNameTextView.setText("No display name available");
            displayLastSignInTextView.setText("");
        }
    }
}