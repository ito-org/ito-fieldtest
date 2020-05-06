package org.itoapp.fieldtest.datasource;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.lang.reflect.Type;

import static android.content.Context.SENSOR_SERVICE;

public class MagneticField implements DataSource, SensorEventListener {
    private SensorManager sensorManager;
    private int accuracy;
    private float magnetic_x, magnetic_y, magnetic_z;

    @Override
    public boolean setup(Context context) {
        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);

        if (sensorManager == null) {
            return false;
        }

        Sensor magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if (magnetometer == null) {
            return false;
        }

        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_FASTEST);

        return true;
    }

    @Override
    public String[] getDataLabels() {
        return new String[]{"magnetic_x", "magnetic_y", "magnetic_z", "magnetic_accuracy"};
    }

    @Override
    public Type[] getDataTypes() {
        return new Type[]{float.class, float.class, float.class, int.class};
    }

    @Override
    public Object[] getData() {
        return new Object[]{magnetic_x, magnetic_y, magnetic_z, accuracy};
    }

    @Override
    public void destroy() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        magnetic_x = event.values[0];
        magnetic_y = event.values[1];
        magnetic_z = event.values[2];
        accuracy = event.accuracy;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }
}
