package com.comp90018.comp90018.service;
import android.net.Uri;
import android.util.Log;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class ImageUploadService {

    private FirebaseStorage storage = FirebaseStorage.getInstance();

    public interface UploadCallback {
        void onSuccess(String imageUrl);
        void onFailure(Exception e);
    }

    public interface DeleteCallback {
        void onSuccess();
        void onFailure(Exception e);
    }


    public void uploadImageToFirebase(File photoFile, UploadCallback callback) {
        Uri fileUri = Uri.fromFile(photoFile);
        StorageReference storageRef = storage.getReference();

        // Create a reference to "images/<filename>"
        StorageReference imagesRef = storageRef.child("images/" + photoFile.getName());

        // Upload the file to Firebase
        imagesRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL
                    imagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        Log.d("ImageUploadService", "Image URL: " + imageUrl);
                        callback.onSuccess(imageUrl);
                    }).addOnFailureListener(e -> {
                        Log.e("ImageUploadService", "Failed to get download URL", e);
                        callback.onFailure(e);
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("ImageUploadService", "Image upload failed", e);
                    callback.onFailure(e);
                });
    }

    public void deleteImageFromFirebase(String imageUrl, DeleteCallback callback) {
        // Get the storage reference using the image URL
        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);

        // Delete the file
        imageRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("ImageUploadService", "Image successfully deleted");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e("ImageUploadService", "Failed to delete image", e);
                    callback.onFailure(e);
                });
    }
}