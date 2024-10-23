package com.comp90018.comp90018;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Toast;

public class StepCountActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_ACTIVITY_RECOGNITION = 1;
    private TextView stepCountTextView;
    private BroadcastReceiver stepCountReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_count);

        stepCountTextView = findViewById(R.id.stepCountTextView);

        // Start the StepCountService
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, REQUEST_CODE_ACTIVITY_RECOGNITION);
        } else {
            startStepCountService();
        }

        // Initialize the BroadcastReceiver to receive step count updates
        stepCountReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null && "com.comp90018.comp90018.STEP_COUNT_UPDATE".equals(intent.getAction())) {
                    int stepCount = intent.getIntExtra("stepCount", 0);
                    stepCountTextView.setText("Steps: " + stepCount);
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register the receiver to listen for step count updates
        IntentFilter filter = new IntentFilter("com.comp90018.comp90018.STEP_COUNT_UPDATE");
        registerReceiver(stepCountReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the receiver to avoid memory leaks
        unregisterReceiver(stepCountReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_ACTIVITY_RECOGNITION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startStepCountService();
            } else {
                Toast.makeText(this, "Permission denied. Step counting will not work.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startStepCountService() {
        Intent stepServiceIntent = new Intent(this, com.comp90018.comp90018.service.StepCountService.class);
        startService(stepServiceIntent);
    }
}