package org.itoapp.fieldtest.datasource;

import android.hardware.Sensor;


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

}
