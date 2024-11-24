//package com.example.cardspeeddetector;//package com.example.cardspeeddetector;//package com.example.speedometer;
//import android.Manifest;
//import android.content.Context;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.VibrationEffect;
//import android.os.Vibrator;
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//import androidx.core.app.ActivityCompat;
//
//public class MotionTracker {
//    private final Context context;
//    private final LocationManager locationManager;
//    private LocationListener locationListener;
//    private final MotionTrackerListener motionTrackerListener;
//    private boolean isMotionActive = false; // Flag to track if motion is active
//    private boolean isVibrating = false; // Flag to track if vibration is active
//
//    // Constructor to initialize context and listener
//    public MotionTracker(Context context, MotionTrackerListener listener) {
//        this.context = context;
//        this.motionTrackerListener = listener;
//        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//        initializeLocationListener();
//    }
//
//    // Initialize the location listener
//    private void initializeLocationListener() {
//        locationListener = new LocationListener() {
//            @Override
//            public void onLocationChanged(@NonNull Location location) {
//                float speed = location.getSpeed(); // Speed in meters/second
//                double latitude = location.getLatitude();
//                double longitude = location.getLongitude();
//                float accuracy = location.getAccuracy();
//
//                // Notify the listener with the updated data
//                if (motionTrackerListener != null) {
//                    motionTrackerListener.onMotionDetected(speed, latitude, longitude, accuracy);
//                }
//
//                // Log motion start/stop based on speed
//                if (speed > 3.10f) {  // If speed exceeds threshold, consider it as motion start
//                    if (!isMotionActive) {
//                        isMotionActive = true; // Motion started
//                        Log.d("MotionTracker", "Motion started! Speed: " + speed + " m/s");
//                        startVibration();  // Trigger vibration when motion starts
//                    }
//                } else {  // If speed drops below threshold, consider it as motion stop
//                    if (isMotionActive) {
//                        isMotionActive = false; // Motion stopped
//                        Log.d("MotionTracker", "Motion stopped. Speed: " + speed + " m/s");
//                    }
//                }
//            }
//
//            @Override
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//                // No action needed
//            }
//
//            @Override
//            public void onProviderEnabled(@NonNull String provider) {
//                // No action needed
//            }
//
//            @Override
//            public void onProviderDisabled(@NonNull String provider) {
//                // No action needed
//            }
//        };
//    }
//
//    // Start tracking motion (request location updates)
//    public void startTracking() {
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // Handle permission request
//            return;
//        }
//
//        // Request location updates from the GPS provider
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener); // Every second, min distance of 1 meter
//    }
//
//    // Stop tracking motion
//    public void stopTracking() {
//        locationManager.removeUpdates(locationListener);
//    }
//
//    // Trigger vibration when motion starts
//    private void startVibration() {
//        // Only vibrate if not already vibrating
//        if (!isVibrating) {
//            isVibrating = true;
//
//            // Get the Vibrator system service
//            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
//
//            // Check if vibrator service is available and supports vibration
//            if (vibrator != null && vibrator.hasVibrator()) {
//                // For devices running Android 26 (Oreo) or higher, use VibrationEffect
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                    vibrator.vibrate(VibrationEffect.createOneShot(2000, VibrationEffect.DEFAULT_AMPLITUDE)); // 2000ms = 2 seconds
//                } else {
//                    // For older versions, use the deprecated vibrate method
//                    vibrator.vibrate(2000); // 2000ms = 2 seconds
//                }
//            } else {
//                Log.d("MotionTracker", "Vibration not supported");
//            }
//
//            // Reset the vibration flag after 2 seconds (duration of the vibration)
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    isVibrating = false;  // Allow vibration again after the delay
//                }
//            }, 2000);  // 2000ms = 2 seconds
//        }
//    }
//
//    // Interface to notify the caller about motion events
//    public interface MotionTrackerListener {
//        void onMotionDetected(float speed, double latitude, double longitude, float accuracy);
//    }
//}
package com.example.cardspeeddetector;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class MotionTracker {
    private final Context context;
    private final LocationManager locationManager;
    private LocationListener locationListener;
    private final MotionTrackerListener motionTrackerListener;

    // Constructor to initialize context and listener
    public MotionTracker(Context context, MotionTrackerListener listener) {
        this.context = context;
        this.motionTrackerListener = listener;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        initializeLocationListener();
    }

    // Initialize the location listener
    private void initializeLocationListener() {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                float speed = location.getSpeed(); // Speed in meters/second
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                float accuracy = location.getAccuracy();

                // Notify the listener with the updated data
                if (motionTrackerListener != null) {
                    motionTrackerListener.onMotionDetected(speed, latitude, longitude, accuracy);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // No action needed
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
                // No action needed
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                // No action needed
            }
        };
    }

    // Start tracking motion (request location updates)
    public void startTracking() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Handle permission request
            return;
        }

        // Request location updates from the GPS provider
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener); // Every second, min distance of 1 meter
    }

    // Stop tracking motion
    public void stopTracking() {
        locationManager.removeUpdates(locationListener);
    }

    // Interface to notify the caller about motion events
    public interface MotionTrackerListener {
        void onMotionDetected(float speed, double latitude, double longitude, float accuracy);
    }
}
