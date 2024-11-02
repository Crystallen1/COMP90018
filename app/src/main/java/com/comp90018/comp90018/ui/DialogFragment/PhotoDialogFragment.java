package com.comp90018.comp90018.ui.DialogFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.comp90018.comp90018.R;

public class PhotoDialogFragment extends DialogFragment {
    private String result;
    private String imageUrl;

    public static PhotoDialogFragment newInstance(String result, String imageUrl) {
        PhotoDialogFragment fragment = new PhotoDialogFragment();
        Bundle args = new Bundle();
        args.putString("result", result);
        args.putString("imageUrl",imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_photo_layout, container, false);

        if (getArguments() != null) {
            result = getArguments().getString("result");
            imageUrl = getArguments().getString("imageUrl");
        }

        ImageView imageView = view.findViewById(R.id.imageView);
        TextView textView = view.findViewById(R.id.textView);
        Glide.with(this)
                .load(imageUrl)
                .apply(new RequestOptions().placeholder(R.drawable.ic_heart_outline).error(R.drawable.ic_heart_outline)) // 占位图和错误图
                .into(imageView);
        textView.setText(result);
        // imageView.setImageBitmap(...); // 根据你的实现加载图片

        // 设置关闭按钮
        view.findViewById(R.id.closeButton).setOnClickListener(v -> dismiss());

        return view;
    }
}
