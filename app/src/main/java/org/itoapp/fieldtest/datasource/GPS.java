package org.itoapp.fieldtest.datasource;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.lang.reflect.Type;

public class GPS implements DataSource, LocationListener {

    private LocationManager locationManager;

    private DataListener dataListener;

    // Permission is checked by MainActivity
    @SuppressLint("MissingPermission")
    @Override
    public boolean setup(Context context) {
        locationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager == null) {
            return false;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        return true;
    }

    @Override
    public String[] getDataLabels() {
        return new String[]{"gps_latitude", "gps_longitude", "gps_altitude", "gps_accuracy"};
    }

    @Override
    public Type[] getDataTypes() {
        return new Type[]{double.class, double.class, double.class, float.class};
    }

    @Override
    public void setDataListener(DataListener listener) {
        this.dataListener = listener;
    }

    @Override
    public void destroy() {
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        double altitude = location.getAltitude();
        float accuracy = location.getAccuracy();

        if (dataListener != null) {
            dataListener.onDataReceived(new Object[]{latitude, longitude, altitude, accuracy});
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Do nothing
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Do nothing
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Do nothing
    }
}
