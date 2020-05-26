package org.itoapp.fieldtest.datasource;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

import org.itoapp.fieldtest.TelemetryService;

import java.lang.reflect.Type;
import java.util.Arrays;

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
        String[] result = new String[]{
                "device_model",
                "brand",
                "board",
                "sdk_version",
                "radio_version",
                "own_uuid",
                "ble_advertise_mode",
                "ble_tx_power_level",
                "ble_scan_mode"};
        if (includeSerial) {
            result = Arrays.copyOf(result, result.length + 1);
            result[result.length - 1] = "serial_number";
        }
        return result;
    }

    @Override
    public Type[] getDataTypes() {
        Type[] result = new Type[]{
                String.class,
                String.class,
                String.class,
                int.class,
                String.class,
                byte[].class,
                int.class,
                int.class,
                int.class};
        if (includeSerial) {
            result = Arrays.copyOf(result, result.length + 1);
            result[result.length - 1] = String.class;
        }
        return result;
    }

    @SuppressLint({"NewApi", "MissingPermission"})
    @Override
    public void setDataListener(DataListener listener) {
        if (listener != null) {
            Object[] result = new Object[]{
                    Build.MODEL,
                    Build.BRAND,
                    Build.BOARD,
                    Build.VERSION.SDK_INT,
                    Build.getRadioVersion(),
                    TelemetryService.BROADCAST_ID,
                    TelemetryService.BLE_ADVERTISE_MODE,
                    TelemetryService.BLE_TX_POWER_LEVEL,
                    TelemetryService.BLE_SCAN_MODE};
            if (includeSerial) {
                result = Arrays.copyOf(result, result.length + 1);
                result[result.length - 1] = Build.getSerial();
            }
            listener.onDataReceived(result);
        }
    }
}

