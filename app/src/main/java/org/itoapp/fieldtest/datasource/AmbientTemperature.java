package org.itoapp.fieldtest.datasource;

import android.hardware.Sensor;

public class AmbientTemperature extends SensorDataSource {
    @Override
    public int getSensorType() {
        return Sensor.TYPE_AMBIENT_TEMPERATURE;
    }

    @Override
    public String[] getDataLabels() {
        return new String[]{"ambient_temperature", "ambient_temperature_accuracy"};
    }
}
