package com.example.cardspeeddetector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import com.example.cardspeeddetector.MyService;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Check the stored service status
            boolean isServiceRunning = PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean("service_running", false);

            if (isServiceRunning) {
                // Restart the service if it was previously running
                Intent serviceIntent = new Intent(context, MyService.class);
                context.startService(serviceIntent);
            }
        }
    }
}
