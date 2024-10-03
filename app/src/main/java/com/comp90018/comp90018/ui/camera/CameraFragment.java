package com.comp90018.comp90018.ui.camera;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.view.PreviewView;
import androidx.fragment.app.Fragment;

import com.comp90018.comp90018.R;
import com.comp90018.comp90018.service.GPTService;
import com.comp90018.comp90018.service.ImageCaptureService;
import com.comp90018.comp90018.service.ImageUploadService;
import com.comp90018.comp90018.service.LocationService;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraFragment extends Fragment {

    private ImageCaptureService imageCaptureService;
    private ImageUploadService imageUploadService;
    private PreviewView cameraPreviewView;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Handler mainHandler = new Handler(Looper.getMainLooper());

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
        imageUploadService = new ImageUploadService();
        // 启动 CameraX
        imageCaptureService.startCamera(requireContext(), getViewLifecycleOwner(), cameraPreviewView); // 传递 PreviewView

        // 设置拍照按钮事件
        View captureButton = view.findViewById(R.id.captureButton);
        captureButton.setOnClickListener(v -> {
            // Capture the photo
            imageCaptureService.takePicture(requireContext(), new ImageCaptureService.PhotoCallback() {
                @Override
                public void onPhotoSaved(File photoFile) {
                    // Upload the image after it's saved
                    imageUploadService.uploadImageToFirebase(photoFile, new ImageUploadService.UploadCallback() {
                        @Override
                        public void onSuccess(String imageUrl) {
                            // Now pass the image URL to GPT to get a journey introduction
                            double latitude = 44;  // Obtain latitude
                            double longitude = 10;  // Obtain longitude
                            GPTService gptService = new GPTService();
                            gptService.getImageBasedJourneyIntroduction(imageUrl, latitude, longitude, new GPTService.GPTCallback() {
                                @Override
                                public void onSuccess(String result) {
                                    imageUploadService.deleteImageFromFirebase(imageUrl, new ImageUploadService.DeleteCallback() {
                                        @Override
                                        public void onSuccess() {
                                            Log.d("CameraFragment", "success delete image from firebase");

                                            Log.d("CameraFragment", result);
                                        }
                                        @Override
                                        public void onFailure(Exception e) {
                                            Log.e("CameraFragment", "Error in delete image from firebase", e);
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(String error) {
                                    Log.e("CameraFragment", "Error in GPT request");
                                    Toast.makeText(requireContext(), "Image upload failed", Toast.LENGTH_SHORT).show();

                                }
                            });
//
//                            // 在后台线程中执行GPT请求
//                            executorService.execute(() -> {
//                                try {
//                                    // 创建GPTService实例并调用方法
//                                    GPTService gptService = new GPTService();
//                                    String result = gptService.getImageBasedJourneyIntroduction(imageUrl, latitude, longitude);
//
//                                    // 使用Handler回到主线程更新UI
//                                    mainHandler.post(() -> {
//                                        imageUploadService.deleteImageFromFirebase(imageUrl, new ImageUploadService.DeleteCallback() {
//                                            @Override
//                                            public void onSuccess() {
//                                                Log.d("CameraFragment", "success delete image from firebase");
//
//                                                Log.d("CameraFragment", result);
//                                            }
//                                            @Override
//                                            public void onFailure(Exception e) {
//                                                Log.e("CameraFragment", "Error in delete image from firebase", e);
//                                            }
//                                        });
//                                    });
//                                } catch (Exception e) {
//                                    // 捕获可能的异常并记录
//                                    Log.e("CameraFragment", "Error in GPT request", e);
//                                }
//                            });
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(requireContext(), "Image upload failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(requireContext(), "Photo capture failed", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    // 获取 CameraX 预览的 SurfaceProvider
    public PreviewView getSurfaceProvider() {
        return cameraPreviewView;
    }

}