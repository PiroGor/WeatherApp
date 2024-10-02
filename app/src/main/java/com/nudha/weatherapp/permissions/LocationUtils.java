package com.nudha.weatherapp.permissions;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.nudha.weatherapp.API.Meteomatics.requestCreator.WeatherRequest;



public class LocationUtils {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Activity activity;
    private FusedLocationProviderClient fusedLocationClient;

    public LocationUtils(Activity activity) {
        this.activity = activity;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
    }

    public void requestLocation() {
        if (PermissionUtils.checkAndRequestPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_PERMISSION_REQUEST_CODE)) {
            getLocation();
        }
    }

    public void handlePermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionUtils.handlePermissionResult(requestCode, permissions, grantResults, LOCATION_PERMISSION_REQUEST_CODE, new PermissionUtils.PermissionResultListener() {
            @Override
            public void onPermissionGranted() {
                getLocation();
            }

            @Override
            public void onPermissionDenied() {
                Toast.makeText(activity, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return ;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            WeatherRequest.setLocation(location.getLatitude(), location.getLongitude());

                        }else{
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            WeatherRequest.setLocation(latitude, longitude);
                        }
                    }
                });
    }


}
