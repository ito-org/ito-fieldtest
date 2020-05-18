package org.itoapp.fieldtest.datasource;

import android.os.Build;

import org.itoapp.fieldtest.TelemetryService;

import java.lang.reflect.Type;

public class DeviceInformation implements DataSource {
    @Override
    public String[] getDataLabels() {
        return new String[]{"device_model", "sdk_version", "own_uuid"};
    }

    @Override
    public Type[] getDataTypes() {
        return new Type[]{String.class, int.class, byte[].class};
    }

    @Override
    public void setDataListener(DataListener listener) {
        if (listener != null)
            listener.onDataReceived(new Object[]{Build.MODEL, Build.VERSION.SDK_INT, TelemetryService.BROADCAST_ID});
    }
}
