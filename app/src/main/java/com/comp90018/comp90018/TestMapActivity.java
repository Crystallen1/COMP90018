package com.comp90018.comp90018;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;
import androidx.fragment.app.FragmentTransaction;

import com.comp90018.comp90018.R;
import com.comp90018.comp90018.service.ImageCaptureService;
import com.comp90018.comp90018.service.ImageUploadService;
import com.comp90018.comp90018.ui.camera.CameraFragment;
import com.comp90018.comp90018.ui.map.MapFragment;
import com.comp90018.comp90018.utils.PermissionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;

public class TestMapActivity extends AppCompatActivity {
    static final int REQUEST_TAKE_PHOTO = 1;
    private ImageCaptureService imageCaptureService;
    private ImageUploadService imageUploadService;
    private PermissionManager permissionManager;
    private PreviewView cameraPreviewView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_map);

        // 初始化服务类
        imageCaptureService = new ImageCaptureService();
        imageUploadService = new ImageUploadService();
        permissionManager = new PermissionManager(this);  // 确保这里有初始化

        // 加载 MapFragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MapFragment())
                    .commit();
        }

        // 设置 FloatingActionButton 的点击事件
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            if (!permissionManager.hasCameraPermission()) {
                permissionManager.requestCameraPermission();
                if (permissionManager.hasCameraPermission()) {
                    openCameraFragment();
                }
            }else {
                openCameraFragment();
            }
        });

//        // 初始化 FloatingActionButton，并设置点击事件
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(v -> {
//            // 首先检查相机权限
//            if (!permissionManager.hasCameraPermission()) {
//                permissionManager.requestCameraPermission();
//            } else {
//                // 如果相机权限已授予，启动拍照功能
//                // 初始化 CameraX 预览
//                cameraPreviewView = findViewById(R.id.cameraPreviewView);
//                imageCaptureService = new ImageCaptureService();
//
//                // 启动 CameraX
//                imageCaptureService.startCamera(this, this);
////                imageCaptureService.takePicture(this);
//            }
//        });
    }
    // 在 ImageCaptureService 中用于绑定相机预览的 SurfaceProvider
    public PreviewView getSurfaceProvider() {
        return cameraPreviewView;
    }
    private void openCameraFragment() {
        // 替换地图 Fragment 为 CameraFragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new CameraFragment());
        transaction.addToBackStack(null); // 添加到回退栈，按返回按钮时回到地图
        transaction.commit();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            // 获取拍摄的照片文件并上传
            File photoFile = new File(imageCaptureService.getCurrentPhotoPath());
            uploadPhoto(photoFile);
        } else {
            Toast.makeText(this, "Photo capture failed or cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    // 调用 ImageUploadService 上传照片
    private void uploadPhoto(File photoFile) {
        imageUploadService.uploadImage(photoFile, new ImageUploadService.UploadCallback() {
            @Override
            public void onSuccess(String imageUrl) {
                runOnUiThread(() -> {
                    Toast.makeText(TestMapActivity.this, "Photo uploaded! URL: " + imageUrl, Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(TestMapActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}