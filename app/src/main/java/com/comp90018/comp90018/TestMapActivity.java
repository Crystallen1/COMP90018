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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (currentFragment instanceof MapFragment) {
                    openCameraFragment();
                } else if (currentFragment instanceof CameraFragment) {
                    openMapFragment();
                }
            }
        });
    }
    // 在 ImageCaptureService 中用于绑定相机预览的 SurfaceProvider
    public PreviewView getSurfaceProvider() {
        return cameraPreviewView;
    }
    private void openCameraFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        CameraFragment cameraFragment = (CameraFragment) fragmentManager.findFragmentByTag("cameraFragment");

        if (cameraFragment == null) {
            // 如果没有实例化过，则创建新的实例
            cameraFragment = new CameraFragment();
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, cameraFragment, "cameraFragment");
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void openMapFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentByTag("mapFragment");

        if (mapFragment == null) {
            // 如果没有实例化过，则创建新的实例
            mapFragment = new MapFragment();
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, mapFragment, "mapFragment");
        transaction.addToBackStack(null);
        transaction.commit();
    }
}