package org.itoapp.fieldtest.datasource;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import org.itoapp.fieldtest.BleAdvertiser;
import org.itoapp.fieldtest.BleScanner;

import java.lang.reflect.Type;

public class BLE implements DataSource {

    private BleScanner bleScanner;
    private BleAdvertiser bleAdvertiser;
    private DataListener dataListener;

    @Override
    public boolean setup(Context context) {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null)
            return false;
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        bleScanner = new BleScanner(bluetoothAdapter, (id, rssi) -> {
            if (this.dataListener != null)
                this.dataListener.onDataReceived(new Object[]{id, rssi});
        });
        bleAdvertiser = new BleAdvertiser(bluetoothAdapter);

        bleAdvertiser.startAdvertising();
        bleScanner.startScanning();
        return true;
    }

    @Override
    public String[] getDataLabels() {
        return new String[]{"id", "rssi"};
    }

    @Override
    public Type[] getDataTypes() {
        return new Type[]{byte[].class, int.class};
    }

    @Override
    public void setDataListener(DataListener listener) {
        this.dataListener = listener;
    }

    @Override
    public void destroy() {
        if (bleScanner != null)
            try {
                bleScanner.stopScanning();
            } catch (Exception ignored) {
            }
        if (bleAdvertiser != null)
            try {
                bleAdvertiser.stopAdvertising();
            } catch (Exception ignored) {
            }

        dataListener = null;
        bleScanner = null;
        bleAdvertiser = null;
    }
}
