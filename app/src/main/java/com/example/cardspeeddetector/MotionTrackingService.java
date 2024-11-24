package com.example.cardspeeddetector;//package com.example.speedometer;


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
