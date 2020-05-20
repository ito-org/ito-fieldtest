package org.itoapp.fieldtest.datasource;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

import org.itoapp.fieldtest.TelemetryService;

import java.lang.reflect.Type;

public class DeviceInformation implements DataSource {

    private boolean includeSerial = false;

    @Override
    public boolean setup(Context context) {
        includeSerial = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
        return true;
    }

    @Override
    public String[] getDataLabels() {
        if (includeSerial) {
            return new String[]{"device_model", "sdk_version", "radio_version", "serial_number", "own_uuid"};
        } else {
            return new String[]{"device_model", "sdk_version", "radio_version", "own_uuid"};
        }
    }

    @Override
    public Type[] getDataTypes() {
        Build.getRadioVersion();
        if (includeSerial) {
            return new Type[]{String.class, int.class, String.class, String.class, byte[].class};
        } else {
            return new Type[]{String.class, int.class, String.class, byte[].class};
        }
    }

    @SuppressLint({"NewApi", "MissingPermission"})
    @Override
    public void setDataListener(DataListener listener) {
        if (listener != null) {
            if (includeSerial) {
                listener.onDataReceived(new Object[]{Build.MODEL, Build.VERSION.SDK_INT, Build.getRadioVersion(), Build.getSerial(), TelemetryService.BROADCAST_ID});
            } else {
                listener.onDataReceived(new Object[]{Build.MODEL, Build.VERSION.SDK_INT, Build.getRadioVersion(), TelemetryService.BROADCAST_ID});
            }
        }
    }
}

