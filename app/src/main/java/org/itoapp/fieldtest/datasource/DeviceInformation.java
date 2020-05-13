package org.itoapp.fieldtest.datasource;

import android.os.Build;

import java.lang.reflect.Type;

public class DeviceInformation implements DataSource {
    @Override
    public String[] getDataLabels() {
        return new String[]{"device_model", "sdk_version"};
    }

    @Override
    public Type[] getDataTypes() {
        return new Type[]{String.class, int.class};
    }

    @Override
    public Object[] getData() {
        return new Object[]{Build.MODEL, Build.VERSION.SDK_INT};
    }
}
