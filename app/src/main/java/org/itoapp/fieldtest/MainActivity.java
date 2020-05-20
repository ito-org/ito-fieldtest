package org.itoapp.fieldtest;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 1; // value not relevant
    private static final int REQUEST_STORAGE_PERMISSION = 2;
    private static final int REQUEST_READ_PHONE_STATE_PERMISSION = 3;


    private Intent serviceIntent;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, android.content.Intent intent) {
            handleServiceStatus();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        handleServiceStatus();
    }

    void handleServiceStatus() {
        if (Preconditions.canScanBluetooth(this)) {
            startService(serviceIntent);
        } else {
            stopService(serviceIntent);
            if (!Preconditions.hasPhoneStatePermissions(this)) {
                ActivityCompat.requestPermissions(this, new String[]{READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE_PERMISSION);
            } else if (!Preconditions.hasStoragePermissions(this)) {
                ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
            }
            else if(!Preconditions.hasLocationPermissions(this)){
                ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            }
            else if(!Preconditions.isBluetoothEnabled(this)) {
                Toast.makeText(this, "Please enable bluetooth", Toast.LENGTH_SHORT).show();
            }
            else if(!Preconditions.isLocationServiceEnabled(this)) {
                Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serviceIntent = new Intent(this, TelemetryService.class);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(LocationManager.MODE_CHANGED_ACTION);
        registerReceiver(broadcastReceiver, filter);

        handleServiceStatus();
    }
}
