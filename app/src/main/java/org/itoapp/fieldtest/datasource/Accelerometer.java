package org.itoapp.fieldtest.datasource;

import android.hardware.Sensor;

import java.lang.reflect.Type;


public class Accelerometer extends SensorDataSource {

    @Override
    public int getSensorType() {
        return Sensor.TYPE_ACCELEROMETER;
    }

    @Override
    public String[] getDataLabels() {
        return new String[]{"accel_x", "accel_y", "accel_z", "accel_accuracy"};
    }

    @Override
    public Type[] getDataTypes() {
        return new Type[]{float.class, float.class, float.class, int.class};
    }
}
