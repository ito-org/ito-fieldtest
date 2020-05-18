package org.itoapp.fieldtest.datasource;

import android.hardware.Sensor;

import java.lang.reflect.Type;

public class Proximity extends SensorDataSource {
    @Override
    public int getSensorType() {
        return Sensor.TYPE_PROXIMITY;
    }

    @Override
    public String[] getDataLabels() {
        return new String[]{"proximity", "proximity_accuracy"};
    }

    @Override
    public Type[] getDataTypes() {
        return new Type[]{float.class, int.class};
    }
}
