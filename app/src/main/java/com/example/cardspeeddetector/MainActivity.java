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
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;


import android.Manifest;

public class MainActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    static TextView textView;
    private BroadcastReceiver speedReceiver;
    private static final int RESULT_ENABLE = 123;

    private DevicePolicyManager devicePolicyManager;
    private ComponentName componentName;
    private SwitchCompat deviceAdminSwitch;

    private boolean isAdminOn;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    String password;
    private static final int BATTERY_OPTIMIZATION_REQUEST_CODE = 200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
//        password = getSavedPassword();
//
//        Log.d("Passord" , password);
//        textView = findViewById(R.id.textView1);
        Button button1 = findViewById(R.id.button1);
        Button button2 = findViewById(R.id.button2);
//        Button lockButton = findViewById(R.id.button_shutdown);


//        Toast.makeText(this, "password" +password, Toast.LENGTH_SHORT).show();
        deviceAdminSwitch = findViewById(R.id.admin_switch);

        checkAndRequestPermissions(); // Check and request location permissions
        checkAndRequestBatteryOptimization();

        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);

        componentName =  new ComponentName(this,  MyDeviceAdminReceiver.class);

        deviceAdminSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    // Enable Device Admin
                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "For lock screen");
                    startActivityForResult(intent, RESULT_ENABLE);
                } else {
                    // Prompt for Password
                    promptForPasswordAndDisableAdmin();
                }
            }
        });

//        deviceAdminSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if(b){
//                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
//                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
//                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "For lock screen");
//                    startActivityForResult(intent, RESULT_ENABLE);
//                }
//                else{
//                    devicePolicyManager.removeActiveAdmin(componentName);
//                }
//            }
//        });


//        textView.setText("Current Time: " + new Date());

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

//        lockButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (devicePolicyManager.isAdminActive(componentName)) {
//                    // Lock the device
//                    devicePolicyManager.lockNow();
//                } else {
//                    // Inform the user that admin privileges are not enabled
//                    Toast.makeText(MainActivity.this, "You need to enable device admin..!", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
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

        // Check the status of the service
        boolean isServiceRunning = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("service_running", false);

        if (isServiceRunning) {
            Log.d("MainActivity", "Service is running");
        } else {
            Log.d("MainActivity", "Service is stopped");
        }

        isAdminOn = devicePolicyManager.isAdminActive(componentName);
        deviceAdminSwitch.setChecked(isAdminOn);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putBoolean("service_running", false)
                .apply();
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




    private void promptForPasswordAndDisableAdmin() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Enter Password");

        final android.widget.EditText passwordInput = new android.widget.EditText(this);
        passwordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(passwordInput);

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            String enteredPassword = passwordInput.getText().toString().trim();
            // Replace "admin123" with your stored password logic
            if (enteredPassword.equals(getSavedPassword())) {
                // Disable Device Admin
                devicePolicyManager.removeActiveAdmin(componentName);
                Toast.makeText(MainActivity.this, "Admin permissions disabled.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Incorrect password!", Toast.LENGTH_SHORT).show();
                deviceAdminSwitch.setChecked(true); // Re-enable switch since admin wasn't disabled
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
            deviceAdminSwitch.setChecked(true); // Re-enable switch since action was cancelled
        });

        builder.setCancelable(false);
        builder.show();
    }

    private String getSavedPassword() {
        android.content.SharedPreferences sharedPreferences = getSharedPreferences("AdminPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("admin_password", "default123"); // Default password if none is set
    }
}
