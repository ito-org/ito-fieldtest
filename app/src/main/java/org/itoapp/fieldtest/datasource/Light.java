package org.itoapp.fieldtest.datasource;

import android.hardware.Sensor;

public class Light extends SensorDataSource {
    @Override
    public int getSensorType() {
        return Sensor.TYPE_LIGHT;
    }

    @Override
    public String[] getDataLabels() {
        return new String[]{"light_level", "light_accuracy"};
    }
}
