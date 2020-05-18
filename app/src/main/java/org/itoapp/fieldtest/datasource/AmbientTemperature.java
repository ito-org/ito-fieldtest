package org.itoapp.fieldtest.datasource;

import android.hardware.Sensor;

import java.lang.reflect.Type;

public class AmbientTemperature extends SensorDataSource {
    @Override
    public int getSensorType() {
        return Sensor.TYPE_AMBIENT_TEMPERATURE;
    }

    @Override
    public String[] getDataLabels() {
        return new String[]{"ambient_temperature", "ambient_temperature_accuracy"};
    }

    @Override
    public Type[] getDataTypes() {
        return new Type[]{float.class, int.class};
    }
}
