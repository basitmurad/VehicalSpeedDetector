package com.example.cardspeeddetector;//package com.example.cardspeeddetector;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;


import java.util.Date;
import android.Manifest;

public class MainActivity extends AppCompatActivity {
    static TextView textView;
    private BroadcastReceiver speedReceiver;
    private static final int RESULT_ENABLE = 123;

    private DevicePolicyManager devicePolicyManager;
    private ComponentName componentName;
    private SwitchCompat deviceAdminSwitch;

    private boolean isAdminOn;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private static final int BATTERY_OPTIMIZATION_REQUEST_CODE = 200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView1);
        Button button1 = findViewById(R.id.button1);
        Button button2 = findViewById(R.id.button2);
        Button lockButton = findViewById(R.id.button_shutdown);

        deviceAdminSwitch = findViewById(R.id.admin_switch);

        checkAndRequestPermissions(); // Check and request location permissions
        checkAndRequestBatteryOptimization();

        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);

        componentName =  new ComponentName(this,  MyDeviceAdminReceiver.class);

        deviceAdminSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "For lock screen");
                    startActivityForResult(intent, RESULT_ENABLE);
                }
                else{
                    devicePolicyManager.removeActiveAdmin(componentName);
                }
            }
        });


        textView.setText("Current Time: " + new Date());

        Intent intent = new Intent(MainActivity.this, MyService.class);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "Start Service clicked");

                startService(intent);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "Stop Service clicked");
                stopService(intent);            }
        });

        lockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (devicePolicyManager.isAdminActive(componentName)) {
                    // Lock the device
                    devicePolicyManager.lockNow();
                } else {
                    // Inform the user that admin privileges are not enabled
                    Toast.makeText(MainActivity.this, "You need to enable device admin..!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @SuppressLint("ObsoleteSdkInt")
    private void checkAndRequestBatteryOptimization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                @SuppressLint("BatteryLife") Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, BATTERY_OPTIMIZATION_REQUEST_CODE);
            }
        }
    }



    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_ENABLE:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "You have enabled device admin features", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Problem enabling device admin features", Toast.LENGTH_SHORT).show();
                }
                break;
            case BATTERY_OPTIMIZATION_REQUEST_CODE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    String packageName = getPackageName();
                    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

                    if (pm.isIgnoringBatteryOptimizations(packageName)) {
                        Toast.makeText(this, "App excluded from battery optimizations", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to exclude app from battery optimizations", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case RESULT_ENABLE:
//                if (resultCode == Activity.RESULT_OK) {
//                    Toast.makeText(this, "You have enabled device admin features", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(this, "Problem to enable device admin features", Toast.LENGTH_SHORT).show();
//                }
//                break;
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
        isAdminOn = devicePolicyManager.isAdminActive(componentName);
        deviceAdminSwitch.setChecked(isAdminOn);
    }

    private void checkAndRequestPermissions() {
        // Check and request notification permissions for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }

        // Check and request location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permissions", "All permissions granted!");
            } else {
                Toast.makeText(this, "Permissions not granted. App functionality may be limited.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
