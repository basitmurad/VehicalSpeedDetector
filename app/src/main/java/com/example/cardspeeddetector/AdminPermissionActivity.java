//////package com.example.cardspeeddetector;
//////
//////import android.app.admin.DevicePolicyManager;
//////import android.content.ComponentName;
//////import android.content.Intent;
//////import android.content.SharedPreferences;
//////import android.os.Bundle;
//////import android.text.TextUtils;
//////import android.view.View;
//////import android.widget.Toast;
//////
//////import androidx.activity.EdgeToEdge;
//////import androidx.appcompat.app.AppCompatActivity;
//////import androidx.core.graphics.Insets;
//////import androidx.core.view.ViewCompat;
//////import androidx.core.view.WindowInsetsCompat;
//////
//////import com.example.cardspeeddetector.databinding.ActivityAdminPermissionBinding;
//////
//////public class AdminPermissionActivity extends AppCompatActivity {
//////
//////    ActivityAdminPermissionBinding binding;
//////
//////    private static final int RESULT_ENABLE = 123;
//////    private DevicePolicyManager devicePolicyManager;
//////    private ComponentName componentName;
//////    @Override
//////    protected void onCreate(Bundle savedInstanceState) {
//////        super.onCreate(savedInstanceState);
//////
//////binding = ActivityAdminPermissionBinding.inflate(getLayoutInflater());
//////        setContentView(binding.getRoot());
//////
//////
//////        if (isPasswordSet()) {
//////            navigateToNextScreen();
//////        }
//////        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
//////        componentName = new ComponentName(this, MyDeviceAdminReceiver.class);
//////
//////        binding.grantAdminButton.setOnClickListener(new View.OnClickListener() {
//////            @Override
//////            public void onClick(View view) {
//////                String enteredPassword =binding.passwordInput.getText().toString().trim();
//////
//////                if (TextUtils.isEmpty(enteredPassword)) {
//////                    // If password is empty, prompt user to enter it
//////                    Toast.makeText(AdminPermissionActivity.this, "Please enter a password!", Toast.LENGTH_SHORT).show();
//////                } else {
//////                    // Assuming "admin123" is the predefined password
//////                    if (enteredPassword.equals("admin123")) {
//////                        // Trigger admin privileges grant
//////                        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
//////                        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
//////                        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Admin access required for enhanced features.");
//////                        startActivityForResult(intent, RESULT_ENABLE);
//////                    } else {
//////                        Toast.makeText(AdminPermissionActivity.this, "Incorrect password!", Toast.LENGTH_SHORT).show();
//////                    }
//////                }
//////            }
//////        });
//////
//////
//////    }
//////
//////
//////    private void savePassword(String password) {
//////        SharedPreferences sharedPreferences = getSharedPreferences("AdminPrefs", MODE_PRIVATE);
//////        SharedPreferences.Editor editor = sharedPreferences.edit();
//////        editor.putString("admin_password", password);
//////        editor.apply();
//////    }
//////
//////    private boolean isPasswordSet() {
//////        SharedPreferences sharedPreferences = getSharedPreferences("AdminPrefs", MODE_PRIVATE);
//////        return sharedPreferences.contains("admin_password");
//////    }
//////
//////    private void navigateToNextScreen() {
//////        Intent intent = new Intent(AdminPermissionActivity.this, MainActivity.class);
//////        startActivity(intent);
//////        finish();
//////    }
//////}
////
////package com.example.cardspeeddetector;
////
////import android.app.admin.DevicePolicyManager;
////import android.content.ComponentName;
////import android.content.Context;
////import android.content.Intent;
////import android.content.SharedPreferences;
////import android.os.Bundle;
////import android.text.TextUtils;
////import android.util.Log;
////import android.view.View;
////import android.widget.Toast;
////
////import androidx.appcompat.app.AppCompatActivity;
////
////import com.example.cardspeeddetector.databinding.ActivityAdminPermissionBinding;
////
////public class AdminPermissionActivity extends AppCompatActivity {
////
////    private static final int RESULT_ENABLE = 123;
////    private DevicePolicyManager devicePolicyManager;
////    private ComponentName componentName;
////    ActivityAdminPermissionBinding binding;
////
////    String password;
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////
////        binding = ActivityAdminPermissionBinding.inflate(getLayoutInflater());
////        setContentView(binding.getRoot());
////
////        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
////        componentName = new ComponentName(this, MyDeviceAdminReceiver.class);
////
////
////        // Check if password is already set
////        if (isPasswordSet()) {
////            // If password is already set, proceed to admin privileges
////            requestAdminPrivileges();
////        } else {
////            // If no password, prompt user to set a password
////            binding.grantAdminButton.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View view) {
////                    String enteredPassword = binding.passwordInput.getText().toString().trim();
////
////                    if (TextUtils.isEmpty(enteredPassword)) {
////                        Toast.makeText(AdminPermissionActivity.this, "Please enter a password!", Toast.LENGTH_SHORT).show();
////                    } else {
////                        // Save the password and request admin privileges
////                        savePassword(enteredPassword);
////                        requestAdminPrivileges();
////                    }
////                }
////            });
////        }
////    }
////
////    private void savePassword(String password) {
////        SharedPreferences sharedPreferences = getSharedPreferences("AdminPrefs", MODE_PRIVATE);
////        SharedPreferences.Editor editor = sharedPreferences.edit();
////        editor.putString("admin_password", password);
////        editor.apply();
////    }
////
////    private boolean isPasswordSet() {
////        SharedPreferences sharedPreferences = getSharedPreferences("AdminPrefs", MODE_PRIVATE);
////        return sharedPreferences.contains("admin_password");
////    }
////
////    private void requestAdminPrivileges() {
////        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
////        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
////        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Admin access is required for enhanced features.");
////        startActivityForResult(intent, RESULT_ENABLE);
////    }
////
////    @Override
////    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
////        super.onActivityResult(requestCode, resultCode, data);
////        if (requestCode == RESULT_ENABLE) {
////            if (resultCode == RESULT_OK) {
////                Toast.makeText(this, "Admin privileges granted!", Toast.LENGTH_SHORT).show();
////                navigateToNextScreen();
////            } else {
////                Toast.makeText(this, "Admin privileges not granted!", Toast.LENGTH_SHORT).show();
////            }
////        }
////    }
////
////    private void navigateToNextScreen() {
////        Intent intent = new Intent(AdminPermissionActivity.this, MainActivity.class);
////        startActivity(intent);
////        finish();
////    }
////
////
////}
//package com.example.cardspeeddetector;
//
//import android.app.admin.DevicePolicyManager;
//import android.content.ComponentName;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.cardspeeddetector.databinding.ActivityAdminPermissionBinding;
//
//public class AdminPermissionActivity extends AppCompatActivity {
//
//    private static final int RESULT_ENABLE = 123;
//    private DevicePolicyManager devicePolicyManager;
//    private ComponentName componentName;
//    private ActivityAdminPermissionBinding binding;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        // Bind the UI layout
//        binding = ActivityAdminPermissionBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//
//        // Initialize Device Policy Manager and ComponentName
//        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
//        componentName = new ComponentName(this, MyDeviceAdminReceiver.class);
//
//        // Check if a password is already set
//        if (isPasswordSet()) {
//            // Request admin privileges directly if password exists
//            navigateToNextScreen();
//        } else {
//            // Prompt the user to create a password
//            binding.grantAdminButton.setOnClickListener(view -> {
//                String enteredPassword = binding.passwordInput.getText().toString().trim();
//
//                if (TextUtils.isEmpty(enteredPassword)) {
//                    // Notify user if no password is entered
//                    Toast.makeText(this, "Please enter a password to continue!", Toast.LENGTH_SHORT).show();
//                } else {
//                    // Save the entered password and request admin privileges
//                    savePassword(enteredPassword);
//                    requestAdminPrivileges();
//                }
//            });
//        }
//    }
//
//    // Save the password to SharedPreferences
//    private void savePassword(String password) {
//        SharedPreferences sharedPreferences = getSharedPreferences("AdminPrefs", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("admin_password", password);
//        editor.apply();
//    }
//
//    // Check if a password is already saved
//    private boolean isPasswordSet() {
//        SharedPreferences sharedPreferences = getSharedPreferences("AdminPrefs", MODE_PRIVATE);
//        return sharedPreferences.contains("admin_password");
//    }
//
//    // Request admin privileges from the user
//    private void requestAdminPrivileges() {
//        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
//        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
//        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Admin access is required for enhanced features.");
//        startActivityForResult(intent, RESULT_ENABLE);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == RESULT_ENABLE) {
//            if (resultCode == RESULT_OK) {
//                // Navigate to the next screen if admin privileges are granted
//                Toast.makeText(this, "Admin privileges granted!", Toast.LENGTH_SHORT).show();
//                navigateToNextScreen();
//            } else {
//                // Notify the user if admin privileges are not granted
//                Toast.makeText(this, "Admin privileges not granted! Please try again.", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    // Navigate to the next activity
//    private void navigateToNextScreen() {
//        Intent intent = new Intent(AdminPermissionActivity.this, MainActivity.class);
//        startActivity(intent);
//        finish();
//    }
//}
package com.example.cardspeeddetector;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.text.TextUtils;
import android.widget.Toast;
import android.provider.Settings;
import android.location.LocationManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cardspeeddetector.databinding.ActivityAdminPermissionBinding;

public class AdminPermissionActivity extends AppCompatActivity {

    private static final int RESULT_ENABLE = 123;
    private static final int REQUEST_BATTERY_OPTIMIZATION = 124;
    private static final int REQUEST_GPS = 125;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName componentName;
    private ActivityAdminPermissionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Bind the UI layout
        binding = ActivityAdminPermissionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Device Policy Manager and ComponentName
        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this, MyDeviceAdminReceiver.class);

        // Check if password is already set
        if (isPasswordSet()) {
            // Request admin privileges directly if password exists
            navigateToNextScreen();
        } else {
            // Prompt the user to create a password
            binding.grantAdminButton.setOnClickListener(view -> {
                String enteredPassword = binding.passwordInput.getText().toString().trim();

                if (TextUtils.isEmpty(enteredPassword)) {
                    // Notify user if no password is entered
                    Toast.makeText(this, "Please enter a password to continue!", Toast.LENGTH_SHORT).show();
                } else {
                    // Save the entered password and request admin privileges
                    savePassword(enteredPassword);
                    requestAdminPrivileges();
                }
            });
        }

        // Delay the permission requests by 3 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestPermissionsIfNotGranted();
            }
        }, 3000);  // 3-second delay
    }

    // Save the password to SharedPreferences
    private void savePassword(String password) {
        SharedPreferences sharedPreferences = getSharedPreferences("AdminPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("admin_password", password);
        editor.apply();
    }

    // Check if a password is already saved
    private boolean isPasswordSet() {
        SharedPreferences sharedPreferences = getSharedPreferences("AdminPrefs", MODE_PRIVATE);
        return sharedPreferences.contains("admin_password");
    }

    // Request admin privileges from the user
    private void requestAdminPrivileges() {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Admin access is required for enhanced features.");
        startActivityForResult(intent, RESULT_ENABLE);
    }

    // Check and request for Battery Optimization and GPS permissions
    private void requestPermissionsIfNotGranted() {
        // Check Battery Optimization permission
        if (!isBatteryOptimized()) {
            requestBatteryOptimizationPermission();
        }

        // Check GPS permission
        if (!isGpsEnabled()) {
            requestGpsPermission();
        }
    }

    // Check if battery optimization is ignored
    private boolean isBatteryOptimized() {
        String packageName = getPackageName();
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        return powerManager.isIgnoringBatteryOptimizations(packageName);
    }

    // Request Battery Optimization permission
    private void requestBatteryOptimizationPermission() {
        Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQUEST_BATTERY_OPTIMIZATION);
    }

    // Check if GPS is enabled
    private boolean isGpsEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    // Request GPS permission
    private void requestGpsPermission() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, REQUEST_GPS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_ENABLE) {
            if (resultCode == RESULT_OK) {
                // Navigate to the next screen if admin privileges are granted
                Toast.makeText(this, "Admin privileges granted!", Toast.LENGTH_SHORT).show();
                navigateToNextScreen();
            } else {
                // Notify the user if admin privileges are not granted
                Toast.makeText(this, "Admin privileges not granted! Please try again.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_BATTERY_OPTIMIZATION) {
            // Handle result of battery optimization request
            if (isBatteryOptimized()) {
                Toast.makeText(this, "Battery optimization is disabled.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_GPS) {
            // Handle result of GPS request
            if (isGpsEnabled()) {
                Toast.makeText(this, "GPS is enabled.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Navigate to the next activity
    private void navigateToNextScreen() {
        Intent intent = new Intent(AdminPermissionActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
