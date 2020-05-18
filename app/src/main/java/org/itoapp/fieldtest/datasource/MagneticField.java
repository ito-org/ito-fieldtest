package org.itoapp.fieldtest.datasource;

import android.hardware.Sensor;

import java.lang.reflect.Type;


public class MagneticField extends SensorDataSource {
    @Override
    public final int getSensorType() {
        return Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED;
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
}
