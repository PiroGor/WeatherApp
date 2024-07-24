package com.nudha.weatherapp.permissions;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtils {
    public static boolean checkAndRequestPermission(Activity activity, String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(activity, permission)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
            return false;
        }
        return true;
    }

    public static void handlePermissionResult(int requestCode, String[] permissions, int[] grantResults, int expectedRequestCode, PermissionResultListener listener) {
        if (requestCode == expectedRequestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                listener.onPermissionGranted();
            } else {
                listener.onPermissionDenied();
            }
        }
    }

    public interface PermissionResultListener {
        void onPermissionGranted();
        void onPermissionDenied();
    }
}
