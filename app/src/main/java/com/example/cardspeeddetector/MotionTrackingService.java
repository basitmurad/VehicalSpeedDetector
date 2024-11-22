package com.example.cardspeeddetector;//package com.example.speedometer;
//
//import android.annotation.SuppressLint;
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//import android.os.IBinder;
//import androidx.annotation.Nullable;
//import androidx.core.app.NotificationCompat;
//
//public class MotionTrackingService extends android.app.Service implements MotionTracker.MotionTrackerListener {
//
//    private MotionTracker motionTracker;
//
//    @SuppressLint("ForegroundServiceType")
//    @Override
//    public void onCreate() {
//        super.onCreate();
//
//        // Start tracking motion as soon as the service is created
//        motionTracker = new MotionTracker(this, this);
//        motionTracker.startTracking();
//
//        // Create a notification channel for Android 8.0 (Oreo) and above
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(
//                    "motion_tracking_channel",
//                    "Motion Tracking",
//                    NotificationManager.IMPORTANCE_DEFAULT
//            );
//            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        // Create a foreground notification
//        Notification notification = new NotificationCompat.Builder(this, "motion_tracking_channel")
//                .setContentTitle("Tracking Motion")
//                .setContentText("Motion tracking is active in the background")
//                .setSmallIcon(R.drawable.ic_launcher_background)  // Replace with your icon
//                .build();
//
//        // Start the service in the foreground
//        startForeground(1, notification);
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        // This method is called when the service is started
//        // You can restart the service if it gets terminated
//        return START_STICKY;
//    }
//
//    @Override
//    public void onMotionDetected(float speed, double latitude, double longitude, float accuracy) {
//        // Handle motion detection logic here, as you would in the activity
//        // Example: Log motion, vibrate, etc.
//        if (speed > 0.1f) {
//            // Detect motion, vibrate, or perform any action
//        }
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        // Stop motion tracking when the service is destroyed
//        if (motionTracker != null) {
//            motionTracker.stopTracking();
//        }
//    }
//}


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MotionTrackingService extends Service implements MotionTracker.MotionTrackerListener {

    private MotionTracker motionTracker;

    @Override
    public void onCreate() {
        super.onCreate();
        // Start tracking motion as soon as the service is created
        motionTracker = new MotionTracker(this, this);
        motionTracker.startTracking();

        // Log that motion tracking service has started
        Log.d("MotionTrackingService", "Motion tracking service started in the background.");
    }

    @Override
    public void onMotionDetected(float speed, double latitude, double longitude, float accuracy) {
        // Log motion detected events
        Log.d("MotionTrackingService", "Speed: " + speed + " m/s, Lat: " + latitude + ", Lon: " + longitude + ", Accuracy: " + accuracy);

        // Detect motion based on speed
        if (speed > 0.1f) {
            Log.d("MotionTrackingService", "Motion detected! Speed: " + speed + " m/s");
        } else {
            Log.d("MotionTrackingService", "No motion detected. Speed is too low.");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop motion tracking when the service is destroyed
        if (motionTracker != null) {
            motionTracker.stopTracking();
        }
        Log.d("MotionTrackingService", "Motion tracking service stopped.");
    }
}
