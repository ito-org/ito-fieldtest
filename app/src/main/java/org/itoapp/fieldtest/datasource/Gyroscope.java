package org.itoapp.fieldtest.datasource;

import android.hardware.Sensor;

public class Gyroscope extends SensorDataSource {
    @Override
    public int getSensorType() {
        return Sensor.TYPE_GYROSCOPE;
    }

    @Override
    public String[] getDataLabels() {
        return new String[]{"gyro_speed_x", "gyro_speed_y", "gyro_speed_z", "gyro_accuracy"};
    }
}
