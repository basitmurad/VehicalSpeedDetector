package com.example.cardspeeddetector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

//package com.example.cardspeeddetector;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//
//
//import android.content.Intent;
//
//public class ScreenStateReceiver extends BroadcastReceiver {
//    private MyService myService;
//
//    public ScreenStateReceiver(MyService service) {
//        this.myService = service;
//    }
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        String action = intent.getAction();
//        if (Intent.ACTION_SCREEN_OFF.equals(action)) {
//            myService.stopTrackingSpeed();
//        } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
//            myService.startTrackingSpeed();
//        }
//    }
//}
//
//
//
//
//
//
public class ScreenStateReceiver extends BroadcastReceiver {
    private MyService myService;

    public ScreenStateReceiver(MyService service) {
        this.myService = service;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_SCREEN_ON.equals(action)) {
            Log.d("ScreenStateReceiver", "Screen is on");
        } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
            Log.d("ScreenStateReceiver", "Screen is off");
        }
        // No action to stop tracking or the service
    }
}
