package com.mac.training.locationmanager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LogWriter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity-->";
    private static final int PERMISSION_LOCATION_REQUEST_CODE = 1;

    long myGPSFrequency=1000;
    LocationManager locationManager;
    LocationListener locationListener;
    String permission;
    String locationProvider;
    EditText etFrequency;
    TextView tvLong;
    TextView tvLat;
    TextView tvTime;
    TextView tvStatus;
    boolean isGPSEnabled;
    boolean isNetworkEnabled;
    Location myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etFrequency = ((EditText) this.findViewById(R.id.etFrequency));
        etFrequency.setText(Long.toString(myGPSFrequency));

        tvTime = ((TextView) this.findViewById(R.id.tvTime));
        tvLong = ((TextView) this.findViewById(R.id.tvLongitude));
        tvLat = ((TextView) this.findViewById(R.id.tvLatitude));
        tvStatus = ((TextView) this.findViewById(R.id.tvStatus));

        Log.w(TAG, "onCreate: ");

        myLocation = null;
        permission = "android.permission.ACCESS_FINE_LOCATION";

    }

//    private boolean checkFineCoarsePermission(String permission) {
//        int res = getApplicationContext().checkCallingOrSelfPermission(permission);
//        return (res == PackageManager.PERMISSION_GRANTED);
//    }

    private void initLocationManager() {

        String status="";
        // Acquire a reference to the system location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null) {
            Log.d(TAG, "MainActivity --> initLocationManager --> Success");
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            locationProvider = locationManager.getBestProvider(criteria, true);
            status = status + "\nLocation Provider: " + locationProvider.toString();

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            status = status + "\nGPS Provider: " + LocationManager.GPS_PROVIDER;
            if (isGPSEnabled) {
                Toast.makeText(getApplicationContext(), "GPS enabled!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "GPS disabled!", Toast.LENGTH_SHORT).show();
            }

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            status = status + "\nNetwork Provider: " + LocationManager.NETWORK_PROVIDER;
            if (isGPSEnabled) {
                Toast.makeText(getApplicationContext(), "Network enabled!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Network disabled!", Toast.LENGTH_SHORT).show();
            }

            // Define a listener that responds to location updates
            locationListener = new LocationListener() {

                @Override
                public void onLocationChanged(Location location) {
                    // Called when a new location is found by the network location provider
                    if (location != null)
                        printLatLong(location);
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {
                    Log.w(TAG, "onStatusChanged: ");
                }

                @Override
                public void onProviderEnabled(String s) {
                    Log.w(TAG, "onProviderEnabled: ");
                    Toast.makeText(getApplicationContext(), "Gps is turned on!! ",
                            Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onProviderDisabled(String s) {
                    Log.w(TAG, "onProviderDisabled: ");
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Gps is turned off!! ",
                            Toast.LENGTH_SHORT).show();
                    resetText("");

                }
            };
            tvStatus.setText(status);

        }
        else {
            Log.d(TAG, "MainActivity --> initLocationManager --> Failed");
        }
    }

    private void printLatLong(Location location) {
        double latitude;
        double longitude;

        SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss ");
        String format = s.format(new Date());

        latitude = location.getLatitude();
        longitude =  location.getLongitude();

        Log.w(TAG, "onLocationChanged: ");
        String msg = "@" + format + "Lat: " + latitude
                + "Lon: " + longitude;

        tvTime.setText("Time:" + format);
        tvLat.setText("Latitude:" + latitude);
        tvLong.setText("Longitude:" + longitude);

        //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    private void setLocationListener() {

        if (isGPSEnabled) {
            Log.d(TAG, "MainActivity --> setLocationListener --> Success");
            try {
                myGPSFrequency = Long.parseLong(etFrequency.getText().toString());
                locationManager.requestLocationUpdates(locationProvider, myGPSFrequency, 0, locationListener);
                //myLocation = locationManager.getLastKnownLocation(locationProvider);

//                if (myLocation == null) {
//                    Toast.makeText(getApplicationContext(), "Attempt to start with frequency " + myGPSFrequency + " failed", Toast.LENGTH_SHORT).show();
//                }else {
//                    Toast.makeText(getApplicationContext(), "Attempt to start with frequency " + myGPSFrequency+ " succeeded", Toast.LENGTH_SHORT).show();
//                }
            } catch (SecurityException e) {

            }
        }
        else {
            Log.d(TAG, "MainActivity --> setLocationListener --> Failed");
        }
        Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
    }

    private void resetText(String s) {
        tvTime.setText("Time:"+s);
        tvLat.setText("Latitude:"+s);
        tvLong.setText("Longitude:"+s);
    }

    public void clickLast(View view) {
        Log.d(TAG, "MainActivity --> clickLast");

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //setLocationListener();

            Toast.makeText(getApplicationContext(), "Provider" +locationProvider, Toast.LENGTH_SHORT).show();
            if (locationManager!= null) {
                myLocation = locationManager.getLastKnownLocation(locationProvider);
                if (myLocation != null) {
                    printLatLong(myLocation);
                }
                else {
                    Log.d(TAG, "MainActivity --> clickLast --> No location to print");
                }
            }
            else {
                Log.d(TAG, "MainActivity --> clickLast --> No location manager available");
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Last: No permissions", Toast.LENGTH_LONG).show();
        }
    }

    public void clickStop(View view) {
        Log.d(TAG, "MainActivity --> clickStop");

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
           // setLocationListener();
            // Remove the listener you previously added
            locationManager.removeUpdates(locationListener);
            resetText("Stopped");
        }
        else {
            Toast.makeText(getApplicationContext(), "Last: No permissions", Toast.LENGTH_LONG).show();
        }
    }

    public void clickStart(View view) {
        double latitude;
        double longitude;

        Log.d(TAG, "MainActivity --> clickStart");

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            initLocationManager();
            setLocationListener();
        } else {
            // Shoul we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // No explanation needed, we can request the permission.
                Toast.makeText(getApplicationContext(), "I am here permissions 2", Toast.LENGTH_LONG).show();

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_LOCATION_REQUEST_CODE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

    }

    public void clickReset(View view) {
        resetText("");
    }
}
