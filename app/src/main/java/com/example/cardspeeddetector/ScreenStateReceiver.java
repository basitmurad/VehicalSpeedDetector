package com.example.cardspeeddetector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

//package com.example.cardspeeddetector;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
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



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenStateReceiver extends BroadcastReceiver {

    private final MyService myService;

    public ScreenStateReceiver(MyService service) {
        myService = service;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_SCREEN_OFF.equals(action)) {
            // Stop tracking speed when the screen is off
            if (myService != null) {
                myService.stopTrackingSpeed();
            }
        } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
            // Start tracking speed when the screen is on
            if (myService != null) {
                myService.startTrackingSpeed();
            }
        }
    }
}



