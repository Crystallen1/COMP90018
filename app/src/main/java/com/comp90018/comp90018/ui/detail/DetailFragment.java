package com.comp90018.comp90018.ui.detail;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.comp90018.comp90018.R;
import com.comp90018.comp90018.model.Journey;

public class DetailFragment extends Fragment {

    private ImageButton btnBackDetail;
    private ImageView imageViewDetail;
    private TextView textViewDetailName, textViewDetailLocation, textViewAboutContent;
    private Button buttonWishlist;
    private NavController navController;


    private boolean isInWishlist = false; // 用于模拟是否在愿望清单中的状态

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 填充布局
        View view = inflater.inflate(R.layout.fragment_attractions_detail, container, false);

        // 初始化视图
        btnBackDetail = view.findViewById(R.id.btnBackDetail);
        imageViewDetail = view.findViewById(R.id.imageView_detail);
        textViewDetailName = view.findViewById(R.id.textView_detail_name);
        textViewDetailLocation = view.findViewById(R.id.textView_detail_location);
        textViewAboutContent = view.findViewById(R.id.textView_about_content);
        buttonWishlist = view.findViewById(R.id.button_wishlist);

        // 设置返回按钮的点击事件
        btnBackDetail.setOnClickListener(v -> {
            // 返回上一个 Fragment
            NavController navController = Navigation.findNavController(v);
            navController.popBackStack();
        });

        // 处理 "Add to Wishlist" 按钮点击事件
        buttonWishlist.setOnClickListener(v -> toggleWishlist());

        // 从 Bundle 中获取数据并显示在界面上
        if (getArguments() != null) {
            Journey journey = getArguments().getParcelable("journey");

            // 设置景点名称、位置和描述
            textViewDetailName.setText(journey.getName());
            textViewDetailLocation.setText(journey.getId());
            textViewAboutContent.setText(journey.getNotes());

            // 使用 Glide 加载图片
            Glide.with(this)
                    .load(journey.getImageUrl())
                    .placeholder(R.drawable.ic_calendar) // 可选的占位符
                    .error(R.drawable.ic_back) // Optional error image
                    .into(imageViewDetail);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(requireView());

    }

    /**
     * 切换愿望清单状态的逻辑
     */
    private void toggleWishlist() {
        if (isInWishlist) {
            // 如果已经在愿望清单中，移除
            buttonWishlist.setText("Add to Wishlist");
            Toast.makeText(getContext(), "Removed from Wishlist", Toast.LENGTH_SHORT).show();
        } else {
            // 如果不在愿望清单中，添加
            buttonWishlist.setText("Remove from Wishlist");
            Toast.makeText(getContext(), "Added to Wishlist", Toast.LENGTH_SHORT).show();
        }

        // 切换状态
        isInWishlist = !isInWishlist;
    }
}
