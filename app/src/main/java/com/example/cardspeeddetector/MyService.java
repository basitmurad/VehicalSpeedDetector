package com.example.cardspeeddetector;////package com.example.cardspeeddetector;//package com.example.cardspeeddetector;//package com.example.cardspeeddetector;//package com.example.cardspeeddetector;//package com.example.cardspeeddetector;//package com.example.cardspeeddetector;////package com.example.cardspeeddetector;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationResult;

public class MyService extends Service implements MotionTracker.MotionTrackerListener {

    private static final String TAG = "MyService";
    private static final String CHANNEL_ID = "SpeedServiceChannel";
    private static final float SPEED_THRESHOLD_KMH = 3.10f; // Threshold set to 1.0 km/h
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private PowerManager.WakeLock wakeLock;

    private boolean isTrackingSpeed = false;
    private MotionTracker motionTracker;
    private ScreenStateReceiver screenStateReceiver;

    private DevicePolicyManager devicePolicyManager;
    private ComponentName deviceAdminReceiver;

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize motion tracker and location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        motionTracker = new MotionTracker(this, this); // Motion tracking setup

        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        deviceAdminReceiver = new ComponentName(this, MyDeviceAdminReceiver.class);
        // Initialize LocationCallback for FusedLocationProviderClient
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {

                for (android.location.Location location : locationResult.getLocations()) {
                    float speed = location.getSpeed() * 3.6f; // Convert m/s to km/h
                    float accuracy = location.getAccuracy(); // Get accuracy in meters

                    // Check for valid speed and accuracy
                    if (accuracy < 50 && speed > SPEED_THRESHOLD_KMH) {
                        Log.d(TAG, "Current speed (GPS): " + speed + " km/h");
                        startVibration();
                        lockDevice();
                        Log.d(TAG, "Speed exceeds threshold. Vibration and phone lock triggered.");
                    }

                }
            }
        };

        // Create notification channel for service
        createNotificationChannel();
        startForegroundService();

        // Register the ScreenStateReceiver
        screenStateReceiver = new ScreenStateReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenStateReceiver, filter);
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if (checkLocationPermissions()) {
            startLocationUpdates();
            motionTracker.startTracking();
            startForegroundService();
        } else {
            Log.e(TAG, "Permissions not granted. Cannot start location updates.");
            // Handle permission denial (e.g., notify user to grant permissions)
            requestPermissions();
        }

        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putBoolean("service_running", true)
                .apply();
        return START_STICKY;
    }

    private void requestPermissions() {
        // If permissions are not granted, we request them.
        // You can create an Intent to open the app settings page
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(android.net.Uri.parse("package:" + getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);  // Prompt user to manually grant permissions.
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        if (screenStateReceiver != null) {
            unregisterReceiver(screenStateReceiver);
        }
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        if (motionTracker != null) {
            motionTracker.stopTracking();
        }

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restart_service");
        sendBroadcast(broadcastIntent);
    }



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Speed Detection Service",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    private void startForegroundService() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Speed Detection Active")
                .setContentText("Monitoring speed and motion in the background.")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();
        startForeground(1, notification); // Ensure the service is in the foreground
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(10000)  // Request updates every 10 seconds
                .setFastestInterval(5000)  // Fastest update interval
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);  // High accuracy

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    // Check if location permissions are granted
    private boolean checkLocationPermissions() {
        boolean hasFineLocationPermission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean hasCoarseLocationPermission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean hasBackgroundLocationPermission = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;

        return hasFineLocationPermission || hasCoarseLocationPermission || hasBackgroundLocationPermission;
    }

    // MotionTracker callback method
    @Override
    public void onMotionDetected(float speed, double latitude, double longitude, float accuracy) {
        Log.d(TAG, "Motion detected. Speed: " + speed + " m/s, Lat: " + latitude + ", Lon: " + longitude);
//
        if (speed > SPEED_THRESHOLD_KMH) {
            releaseWakeLock();

            startVibration();
            lockDevice();
        }
        else{
            acquireWakeLock();

        }


    }

    // Trigger vibration when motion starts
    private void startVibration() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(2000, VibrationEffect.DEFAULT_AMPLITUDE));  // 2 seconds
            } else {
                vibrator.vibrate(2000);  // 2 seconds
            }
        }
    }
    private void releaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            Log.d(TAG, "WakeLock released, screen will go to sleep.");
        }
    }

    private void acquireWakeLock() {
        if (wakeLock == null || !wakeLock.isHeld()) {
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "StepTrackerService::WakeLock");
            wakeLock.acquire();
            Log.d(TAG, "WakeLock acquired, screen will stay on.");
        }
    }
    // Lock the device if motion exceeds threshold
    private void lockDevice() {
        if (devicePolicyManager != null && devicePolicyManager.isAdminActive(deviceAdminReceiver)) {
            devicePolicyManager.lockNow();
            Log.d(TAG, "Device locked.");
        } else {
            Log.e(TAG, "Device admin permission not granted.");
        }
    }

    // Start tracking speed (can be called from activity or broadcast receiver)
    public void startTrackingSpeed() {
        if (!isTrackingSpeed) {
            if (checkLocationPermissions()) {
                startLocationUpdates();
                motionTracker.startTracking();
                isTrackingSpeed = true;
                Log.d(TAG, "Started tracking speed.");
            } else {
                Log.e(TAG, "Permissions not granted. Cannot start tracking.");
                // Handle permission request if needed
            }
        }
    }

    // Stop tracking speed (can be called from activity or broadcast receiver)
    public void stopTrackingSpeed() {
        if (isTrackingSpeed) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
            motionTracker.stopTracking();
            isTrackingSpeed = false;
            Log.d(TAG, "Stopped tracking speed.");
        }
    }





}


////<!--<?xml version="1.0" encoding="utf-8"?>-->
////<!--<device-admin xmlns:android="http://schemas.android.com/apk/res/android">-->
////<!--    <uses-policies>-->
////<!--        <force-lock/>-->
////<!--        <wipe-data />-->
////<!--        <disable-camera />-->
////<!--        <reset-password />-->
////<!--    </uses-policies>-->
////<!--</device-admin>-->
//
//
//
//
