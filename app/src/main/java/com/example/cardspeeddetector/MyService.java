package com.example.cardspeeddetector;//package com.example.cardspeeddetector;//package com.example.cardspeeddetector;//package com.example.cardspeeddetector;////package com.example.cardspeeddetector;
//
//import android.annotation.SuppressLint;
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.Service;
//import android.app.admin.DevicePolicyManager;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.hardware.camera2.CameraAccessException;
//import android.hardware.camera2.CameraManager;
//import android.os.Build;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.Vibrator;
//import android.util.Log;
//
//import androidx.core.app.NotificationCompat;
//
//import java.util.Date;
//
//
//
//public class MyService extends Service {
//    private CameraManager cameraManager;
//    private String cameraId;
//    private boolean isFlashlightOn = false;
//    private Handler handler;
//    private Runnable toggleFlashlightRunnable;
//    private Vibrator vibrator;
//    private DevicePolicyManager devicePolicyManager;
//    private ComponentName componentName;
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        handler = new Handler();
//    }
//
//    @SuppressLint("ForegroundServiceType")
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//
//        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
//
//        componentName =  new ComponentName(this,  MyDeviceAdminReceiver.class);
//        createNotificationChannel();
//
//        Notification notification = new NotificationCompat.Builder(this, "ForegroundServiceChannel")
//                .setContentTitle("Service Running")
//                .setContentText("Flashlight and vibration service is active.")
//                .setSmallIcon(R.drawable.ic_launcher_foreground)
//                .build();
//
//        startForeground(1, notification);
//
//        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
//
//        try {
//            cameraId = cameraManager.getCameraIdList()[0];
//        } catch (CameraAccessException e) {
//            Log.e("MyService", "Error accessing camera", e);
//        }
//
//        toggleFlashlightRunnable = new Runnable() {
//            @Override
//            public void run() {
//                Log.d("MyService", "Runnable executed at: " + new Date());
//
//                if (isFlashlightOn) {
//                    turnOffFlashlight();
//                    handler.postDelayed(this, 10000);
//                } else {
//                    turnOnFlashlight();
//                    handler.postDelayed(this, 2000);
//                }
//            }
//        };
//
//        handler.post(toggleFlashlightRunnable);
//        return START_STICKY;
//    }
//
//    private void turnOnFlashlight() {
//        if (cameraManager != null && cameraId != null) {
//            try {
//                cameraManager.setTorchMode(cameraId, true);
//                isFlashlightOn = true;
//                Lock();
//                Log.d("MyService", "Flashlight ON");
//                vibrate(1500);
//
//            } catch (CameraAccessException e) {
//                Log.e("MyService", "Error turning on flashlight", e);
//            }
//        }
//    }
//
//    private void turnOffFlashlight() {
//        if (cameraManager != null && cameraId != null) {
//            try {
//                cameraManager.setTorchMode(cameraId, false);
//                isFlashlightOn = false;
//                Log.d("MyService", "Flashlight OFF");
//                vibrate(1000);
//            } catch (CameraAccessException e) {
//                Log.e("MyService", "Error turning off flashlight", e);
//            }
//        }
//    }
//
//    private void vibrate(int duration) {
//        if (vibrator != null && vibrator.hasVibrator()) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                vibrator.vibrate(android.os.VibrationEffect.createOneShot(duration, android.os.VibrationEffect.DEFAULT_AMPLITUDE));
//            } else {
//                vibrator.vibrate(duration);
//            }
//        } else {
//            Log.e("MyService", "Device does not support vibration");
//        }
//    }
//    private void Lock() {
//        if (devicePolicyManager.isAdminActive(componentName)) {
//            // Lock the device
//            devicePolicyManager.lockNow();
//        } else {
//            // Inform the user that admin privileges are not enabled
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (handler != null) {
//            handler.removeCallbacks(toggleFlashlightRunnable);
//        }
//        turnOffFlashlight();
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    private void createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel serviceChannel = new NotificationChannel(
//                    "ForegroundServiceChannel",
//                    "Foreground Service Channel",
//                    NotificationManager.IMPORTANCE_DEFAULT
//            );
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            if (manager != null) {
//                manager.createNotificationChannel(serviceChannel);
//            }
//        }
//    }
//
//
//}


import android.annotation.SuppressLint;
import android.Manifest;

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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

public class MyService extends Service implements MotionTracker.MotionTrackerListener {

    private static final String TAG = "MyService";
    private static final String CHANNEL_ID = "SpeedServiceChannel";
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 10f; // 10 meters
    private boolean isTrackingSpeed = true;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private MotionTracker motionTracker;
    private ScreenStateReceiver screenStateReceiver;

    private static final float SPEED_THRESHOLD_KMH = 2.0f; // Updated speed threshold to 2 km/h
    private DevicePolicyManager devicePolicyManager;
    private ComponentName deviceAdminReceiver;

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize location manager and motion tracker
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        motionTracker = new MotionTracker(this, this); // Motion tracking setup
        // Initialize DevicePolicyManager to lock the device
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        deviceAdminReceiver = new ComponentName(this, MyDeviceAdminReceiver.class);

        // Initialize location listener for speed tracking
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                float speed = location.getSpeed() * 3.6f; // Convert m/s to km/h
//                Log.d(TAG, "Current speed (GPS): " + speed + " km/h");
//
//
//                if (speed > SPEED_THRESHOLD_KMH) {
//                    startVibration();
//                    Log.d(TAG, "Speed exceeds threshold. Vibration and phone lock triggered.");
//                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
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
        // Register location updates if permissions are granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
            motionTracker.startTracking(); // Start motion tracking
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister receiver and stop location and motion tracking
        if (screenStateReceiver != null) {
            unregisterReceiver(screenStateReceiver);
        }
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
        if (motionTracker != null) {
            motionTracker.stopTracking();
        }
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

    @SuppressLint("ForegroundServiceType")
    private void startForegroundService() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Speed Detection Active")
                .setContentText("Monitoring speed and motion in the background.")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();
        startForeground(1, notification);
    }

    // MotionTracker callback method
    @Override
    public void onMotionDetected(float speed, double latitude, double longitude, float accuracy) {
        // Handle motion detection events
        Log.d(TAG, "Motion detected. Speed: " + speed + " m/s, Lat: " + latitude + ", Lon: " + longitude);

        // Vibrate when motion is detected
        if (speed > SPEED_THRESHOLD_KMH) { // If speed exceeds threshold, treat as motion detected
            Log.d(TAG, "Motion detected! Speed: " + speed + " m/s");
            startVibration();
            lockDevice();
        } else {
            Log.d(TAG, "No motion detected. Speed is too low.");
        }
    }

    // Trigger vibration when motion starts
    private void startVibration() {
        // Get the Vibrator system service
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Check if vibrator service is available and supports vibration
        if (vibrator != null && vibrator.hasVibrator()) {
            // For devices running Android 26 (Oreo) or higher, use VibrationEffect
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(2000, VibrationEffect.DEFAULT_AMPLITUDE)); // 2000ms = 2 seconds
            } else {
                // For older versions, use the deprecated vibrate method
                vibrator.vibrate(2000); // 2000ms = 2 seconds
            }
        } else {
            Log.d(TAG, "Vibration not supported");
        }
    }

    // Methods to start/stop tracking speed based on screen state
    public void startTrackingSpeed() {
        if (!isTrackingSpeed) {
            // Reinitialize or restart the tracking
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
            motionTracker.startTracking();
            isTrackingSpeed = true;
            Log.d(TAG, "Started tracking speed.");
        }
    }

    private void lockDevice() {
        // Lock device logic if admin permissions are granted
        if (devicePolicyManager != null && devicePolicyManager.isAdminActive(deviceAdminReceiver)) {
            devicePolicyManager.lockNow();
            Log.d(TAG, "Device locked.");
        } else {
            Log.e(TAG, "Device admin permission not granted.");
        }
    }

    public void stopTrackingSpeed() {
        if (isTrackingSpeed) {
            locationManager.removeUpdates(locationListener);
            motionTracker.stopTracking();
            isTrackingSpeed = false;
            Log.d(TAG, "Stopped tracking speed.");
        }
    }
}
