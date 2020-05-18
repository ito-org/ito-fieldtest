package org.itoapp.fieldtest.datasource;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.lang.reflect.Type;

import static android.content.Context.SENSOR_SERVICE;

public abstract class SensorDataSource implements DataSource, SensorEventListener {
    private SensorManager sensorManager;
    private DataListener dataListener;

    private int numberOfFields;

    @Override
    public boolean setup(Context context) {
        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);

        if (sensorManager == null) {
            return false;
        }

        Sensor sensor = sensorManager.getDefaultSensor(getSensorType());

        if (sensor == null) {
            return false;
        }

        // validation: check that the number of fields is consistent
        numberOfFields = getDataLabels().length;

        Type[] dataTypes = getDataTypes();
        if ((getDataTypes().length != numberOfFields)) throw new AssertionError();

        // also check that the types are floats except for the last one which should be an int
        for (int i = 0; i < dataTypes.length - 1; i++)
            if ((dataTypes[i] != float.class)) throw new AssertionError();

        if (dataTypes[dataTypes.length - 1] != int.class) throw new AssertionError();

        return sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void setDataListener(DataListener listener) {
        this.dataListener = listener;
    }

    @Override
    public void destroy() {
        dataListener = null;
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values.length + 1 != numberOfFields) throw new AssertionError();

        Object[] data = new Object[numberOfFields];
        for (int i = 0; i < event.values.length; i++)
            data[i] = event.values[i];
        data[numberOfFields - 1] = event.accuracy;

        if (dataListener != null) {
            dataListener.onDataReceived(data);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }

    public abstract int getSensorType();
}
