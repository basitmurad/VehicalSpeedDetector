package com.example.cardspeeddetector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenUnlockReceiver extends BroadcastReceiver {
    private static final String TAG = "ScreenUnlockReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // This will be triggered when the screen is unlocked
        if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {
            Log.d(TAG, "Screen unlocked");

            // Start the service if it's not already running
            Intent serviceIntent = new Intent(context, MyService.class);
            context.startService(serviceIntent);
        }
    }
}
