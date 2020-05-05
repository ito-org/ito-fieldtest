package org.itoapp.fieldtest.datasource;

import android.os.Build;

import java.lang.reflect.Type;

public class DeviceModel implements DataSource {
    @Override
    public String[] getDataLabels() {
        return new String[]{"device_model"};
    }

    @Override
    public Type[] getDataTypes() {
        return new Type[]{String.class};
    }

    @Override
    public Object[] getData() {
        return new String[]{Build.MODEL};
    }
}
