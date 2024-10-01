package com.comp90018.comp90018;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;
import androidx.fragment.app.FragmentTransaction;

import com.comp90018.comp90018.R;
import com.comp90018.comp90018.service.GPTService;
import com.comp90018.comp90018.service.ImageCaptureService;
import com.comp90018.comp90018.service.ImageUploadService;
import com.comp90018.comp90018.service.LocationService;
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
    private static final String TAG = "TestMapActivity";

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
}