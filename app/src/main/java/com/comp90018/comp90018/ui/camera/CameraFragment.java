package com.comp90018.comp90018.ui.camera;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.view.PreviewView;
import androidx.fragment.app.Fragment;

import com.comp90018.comp90018.R;
import com.comp90018.comp90018.service.ImageCaptureService;

public class CameraFragment extends Fragment {

    private ImageCaptureService imageCaptureService;
    private PreviewView cameraPreviewView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 使用一个独立的布局文件来加载相机预览
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化 CameraX 预览
        cameraPreviewView = view.findViewById(R.id.cameraPreviewView);

        if (cameraPreviewView == null) {
            Log.e("CameraFragment", "PreviewView is null");
            return;
        }
        imageCaptureService = new ImageCaptureService();

        // 启动 CameraX
        imageCaptureService.startCamera(requireContext(), getViewLifecycleOwner(), cameraPreviewView); // 传递 PreviewView

        // 设置拍照按钮事件
        View captureButton = view.findViewById(R.id.captureButton);
        captureButton.setOnClickListener(v -> {
            // 拍照
            imageCaptureService.takePicture(requireContext());
        });
    }

    // 获取 CameraX 预览的 SurfaceProvider
    public PreviewView getSurfaceProvider() {
        return cameraPreviewView;
    }
}