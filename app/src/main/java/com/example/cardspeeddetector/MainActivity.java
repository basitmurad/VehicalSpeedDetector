package com.example.cardspeeddetector;//package com.example.cardspeeddetector;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;


import android.Manifest;

public class MainActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    private static final int RESULT_ENABLE = 123;
    public static final String SERVICE_RUNNING_KEY = "service_running";

    private DevicePolicyManager devicePolicyManager;
    private ComponentName componentName;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;


    private final BroadcastReceiver serviceStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Check the service status based on shared preferences
            checkServiceStatus();
        }
    };
    private static final int BATTERY_OPTIMIZATION_REQUEST_CODE = 200;
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter();
        filter.addAction("restart_service");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(serviceStatusReceiver, filter, Context.RECEIVER_EXPORTED);
        }

        Button button1 = findViewById(R.id.button1);
        Button button2 = findViewById(R.id.button2);



        checkAndRequestPermissions(); // Check and request location permissions
        checkAndRequestBatteryOptimization();

        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);

        componentName =  new ComponentName(this,  MyDeviceAdminReceiver.class);




        Intent intent = new Intent(MainActivity.this, MyService.class);


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "Start Service clicked");

                // Check if device admin permission is granted
                if (devicePolicyManager.isAdminActive(componentName)) {
                    // If admin permission is granted, start the service
                    SharedPreferences prefs = getSharedPreferences("com.example.cardspeeddetector", MODE_PRIVATE);
                    prefs.edit().putBoolean(SERVICE_RUNNING_KEY, true).apply();
                    startService(intent);
                    Toast.makeText(MainActivity.this, "Service started", Toast.LENGTH_SHORT).show();
                } else {
                    // If admin permission is not granted, request it
                    Intent adminIntent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    adminIntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
                    adminIntent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "This app requires admin privileges to function properly.");
                    startActivityForResult(adminIntent, RESULT_ENABLE);
                }
            }
        });


        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptForPasswordAndStopService();


                }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        checkAndRequestPermissions(); // Check and request location permissions

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
                    Toast.makeText(this, "Admin permission granted.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Admin permission not granted. Please enable to proceed.", Toast.LENGTH_SHORT).show();
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




    @Override
    protected void onResume() {
        super.onResume();

        checkServiceStatus();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(serviceStatusReceiver);
        Intent intent = new Intent(MainActivity.this, MyService.class);

        startService(intent);

    }

    @SuppressLint("ObsoleteSdkInt")
    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.FOREGROUND_SERVICE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    requestPermissions(new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.FOREGROUND_SERVICE_LOCATION
                    }, LOCATION_PERMISSION_REQUEST_CODE);
                }
            }
        }
    }


    private void promptForPasswordAndStopService() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Enter Password");

        final android.widget.EditText passwordInput = new android.widget.EditText(this);
        passwordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(passwordInput);

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            String enteredPassword = passwordInput.getText().toString().trim();

            if (enteredPassword.equals(getSavedPassword())) {
                // Stop the service
                SharedPreferences prefs = getSharedPreferences("com.example.cardspeeddetector", MODE_PRIVATE);
                prefs.edit().putBoolean(SERVICE_RUNNING_KEY, false).apply();

                Intent intent = new Intent(MainActivity.this, MyService.class);
                stopService(intent);

                Toast.makeText(MainActivity.this, "Service stopped.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Incorrect password!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.setCancelable(false);
        builder.show();
    }



    private String getSavedPassword() {
        android.content.SharedPreferences sharedPreferences = getSharedPreferences("AdminPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("admin_password", "default123"); // Default password if none is set
    }

    private void checkServiceStatus() {
        SharedPreferences prefs = getSharedPreferences("com.example.cardspeeddetector", MODE_PRIVATE);
        boolean isServiceRunning = prefs.getBoolean(SERVICE_RUNNING_KEY, false);

        if (isServiceRunning) {
            Toast.makeText(this, "Service is running", Toast.LENGTH_SHORT).show();
            Log.d("Service" ,"Service is running");
        } else {
            Log.d("Service" ,"Service is stopped");

            Toast.makeText(this, "Service is stopped", Toast.LENGTH_SHORT).show();
        }
    }
}
