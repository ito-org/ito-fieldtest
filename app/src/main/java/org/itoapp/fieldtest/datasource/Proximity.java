package org.itoapp.fieldtest.datasource;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.lang.reflect.Type;

import static android.content.Context.SENSOR_SERVICE;

public class Proximity implements DataSource, SensorEventListener {
    private SensorManager sensorManager;
    private DataListener dataListener;

    @Override
    public boolean setup(Context context) {
        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);

        if (sensorManager == null) {
            return false;
        }

        Sensor proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        if (proximitySensor == null) {
            return false;
        }

        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_FASTEST);

        return true;
    }

    @Override
    public String[] getDataLabels() {
        return new String[]{"proximity", "proximity_accuracy"};
    }

    @Override
    public Type[] getDataTypes() {
        return new Type[]{float.class, int.class};
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
        float proximity = event.values[0];
        int accuracy = event.accuracy;

        if (dataListener != null) {
            dataListener.onDataReceived(new Object[]{proximity, accuracy});
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }
}
