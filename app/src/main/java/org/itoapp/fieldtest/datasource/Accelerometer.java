package org.itoapp.fieldtest.datasource;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.lang.reflect.Type;

import static android.content.Context.SENSOR_SERVICE;

public class Accelerometer implements DataSource, SensorEventListener {
    private SensorManager sensorManager;
    private int accuracy;
    private float accel_x, accel_y, accel_z;

    @Override
    public boolean setup(Context context) {
        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);

        if (sensorManager == null) {
            return false;
        }

        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (accelerometer == null) {
            return false;
        }

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);

        return true;
    }

    @Override
    public String[] getDataLabels() {
        return new String[]{"accel_x", "accel_y", "accel_z", "accel_accuracy"};
    }

    @Override
    public Type[] getDataTypes() {
        return new Type[]{float.class, float.class, float.class, int.class};
    }

    @Override
    public Object[] getData() {
        return new Object[]{accel_x, accel_y, accel_z, accuracy};
    }

    @Override
    public void destroy() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        accel_x = event.values[0];
        accel_y = event.values[1];
        accel_z = event.values[2];
        accuracy = event.accuracy;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }
}
