package org.itoapp.fieldtest.datasource;

import android.hardware.Sensor;


public class Accelerometer extends SensorDataSource {

    @Override
    public int getSensorType() {
        return Sensor.TYPE_ACCELEROMETER;
    }

    @Override
    public String[] getDataLabels() {
        return new String[]{"accel_x", "accel_y", "accel_z", "accel_accuracy"};
    }
}
