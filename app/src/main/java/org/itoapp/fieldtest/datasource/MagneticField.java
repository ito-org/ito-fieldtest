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
    private DataListener dataListener;

    @Override
    public boolean setup(Context context) {
        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);

        if (sensorManager == null) {
            return false;
        }

        Sensor magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED);

        if (magnetometer == null) {
            return false;
        }

        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_FASTEST);

        return true;
    }

    @Override
    public String[] getDataLabels() {
        return new String[]{"magnetic_x", "magnetic_y", "magnetic_z",
                "magnetic_bias_x", "magnetic_bias_y", "magnetic_bias_z",
                "magnetic_accuracy"};
    }

    @Override
    public Type[] getDataTypes() {
        return new Type[]{float.class, float.class, float.class,
                float.class, float.class, float.class,
                int.class};
    }

    @Override
    public void setDataListener(DataListener listener) {
        this.dataListener = listener;
    }

    @Override
    public void destroy() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float magnetic_x = event.values[0];
        float magnetic_y = event.values[1];
        float magnetic_z = event.values[2];
        float magnetic_bias_x = event.values[3];
        float magnetic_bias_y = event.values[4];
        float magnetic_bias_z = event.values[5];
        int accuracy = event.accuracy;

        if (dataListener != null) {
            dataListener.onDataReceived(new Object[]{magnetic_x, magnetic_y, magnetic_z,
                    magnetic_bias_x, magnetic_bias_y, magnetic_bias_z,
                    accuracy});
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }
}
