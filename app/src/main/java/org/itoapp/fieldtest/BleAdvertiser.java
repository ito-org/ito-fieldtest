package org.itoapp.fieldtest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.os.Handler;
import android.util.Log;

public class BleAdvertiser {
    private static final String LOG_TAG = "BleAdvertiser";
    private final Handler serviceHandler;

    private BluetoothLeAdvertiser bluetoothLeAdvertiser;
    private AdvertiseCallback bluetoothAdvertiseCallback;

    public BleAdvertiser(BluetoothAdapter bluetoothAdapter) {
        this.bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        this.serviceHandler = new Handler();
    }


    private void restartAdvertising() {
        stopAdvertising();
        startAdvertising();
    }

    public void startAdvertising() {
        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(TelemetryService.BLE_ADVERTISE_MODE)
                .setTxPowerLevel(TelemetryService.BLE_TX_POWER_LEVEL)
                .setConnectable(false)
                .setTimeout(0)
                .build();


        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeTxPowerLevel(false)
                .setIncludeDeviceName(false)
                .addServiceData(TelemetryService.SERVICE_UUID, TelemetryService.BROADCAST_ID)
                .build();

        bluetoothAdvertiseCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
                Log.i(LOG_TAG, "Advertising onStartSuccess");

                // when the timeout expires, restart advertising
                if (settingsInEffect.getTimeout() > 0)
                    serviceHandler.postDelayed(() -> restartAdvertising(),
                            settingsInEffect.getTimeout());
            }

            @Override
            public void onStartFailure(int errorCode) {
                super.onStartFailure(errorCode);
                Log.e(LOG_TAG, "Advertising onStartFailure: " + errorCode);
            }
        };

        bluetoothLeAdvertiser.startAdvertising(settings, data, bluetoothAdvertiseCallback);
    }

    public void stopAdvertising() {
        Log.d(LOG_TAG, "Stopping advertising");
        if (bluetoothAdvertiseCallback != null) {
            bluetoothLeAdvertiser.stopAdvertising(bluetoothAdvertiseCallback);
            bluetoothAdvertiseCallback = null;
        }
    }
}
