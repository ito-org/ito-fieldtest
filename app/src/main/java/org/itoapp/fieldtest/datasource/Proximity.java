package org.itoapp.fieldtest.datasource;

import android.hardware.Sensor;

public class Proximity extends SensorDataSource {
    @Override
    public int getSensorType() {
        return Sensor.TYPE_PROXIMITY;
    }

    @Override
    public String[] getDataLabels() {
        return new String[]{"proximity", "proximity_accuracy"};
    }

}
