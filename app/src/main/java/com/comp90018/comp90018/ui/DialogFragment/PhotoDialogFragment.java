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
        //TODO:这里加入imageUrl里面的东西到imageview里面
        // 加载图片和文字，假设result包含文字并可以加载图片
        textView.setText(result);
        // imageView.setImageBitmap(...); // 根据你的实现加载图片

        // 设置关闭按钮
        view.findViewById(R.id.closeButton).setOnClickListener(v -> dismiss());

        return view;
    }
}
