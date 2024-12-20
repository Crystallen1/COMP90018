package com.comp90018.comp90018.service;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class ImageCaptureService {

    private String currentPhotoPath;
    private ImageCapture imageCapture;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    public String getCurrentPhotoPath() {
        return currentPhotoPath;
    }
    public interface PhotoCallback {
        void onPhotoSaved(File photoFile);
        void onError(Exception e);
    }

    // 初始化 CameraX，并绑定预览和拍照功能
    public void startCamera(Context context, LifecycleOwner lifecycleOwner, PreviewView previewView) {
        cameraProviderFuture = ProcessCameraProvider.getInstance(context);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreviewAndCapture(cameraProvider, previewView, lifecycleOwner);
            } catch (ExecutionException | InterruptedException e) {
                Log.e("ImageCaptureService", "Error starting camera: " + e.getMessage(), e);
            }
        }, ContextCompat.getMainExecutor(context));
    }

    private void bindPreviewAndCapture(ProcessCameraProvider cameraProvider, PreviewView previewView, LifecycleOwner lifecycleOwner) {
        // 预览配置
        Preview preview = new Preview.Builder().build();

        // 设置 SurfaceProvider
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        // 设置 ImageCapture 配置
        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetResolution(new Size(320, 240)) // 设置较低的分辨率
                .build();

        // 选择后置摄像头
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        // 绑定生命周期
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture);
    }
    public void unbindPreviewAndCapture() throws ExecutionException, InterruptedException {
        ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
        if (cameraProvider!=null){
            cameraProvider.unbind();
        }
    }
    // 拍照并保存图片
    public void takePicture(Context context, PhotoCallback callback) {
        File photoFile = null;
        try {
            photoFile = createImageFile(context);
        } catch (IOException e) {
            Log.e("ImageCaptureService", "Error creating image file", e);
        }


        if (photoFile != null) {
            ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();
            File finalPhotoFile = photoFile;
            imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(context), new ImageCapture.OnImageSavedCallback() {
                @Override
                public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                    Log.d("ImageCaptureService", "Photo saved: " + finalPhotoFile.getAbsolutePath());
                    callback.onPhotoSaved(finalPhotoFile);
                }

                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    Log.e("ImageCaptureService", "Photo capture failed: " + exception.getMessage(), exception);
                    callback.onError(exception);
                }
            });
        }
    }

    private File createImageFile(Context context) throws IOException {
        // 以时间戳命名文件
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (storageDir != null && !storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                Log.e("ImageCaptureService", "Failed to create directory: " + storageDir.getAbsolutePath());
                return null;
            }
        }

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        Log.d("ImageCaptureService", "Image file created: " + image.getAbsolutePath());
        return image;
    }
}