package org.itoapp.fieldtest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Build;
import android.util.Log;

public class BleScanner {
    private static final String LOG_TAG = "BleScanner";

    private BluetoothLeScanner bluetoothLeScanner;
    private android.bluetooth.le.ScanCallback bluetoothScanCallback;

    private ScanResultCallback scanResultCallback;

    public BleScanner(BluetoothAdapter bluetoothAdapter, ScanResultCallback callback) {
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        this.scanResultCallback = callback;
    }

    public void startScanning() {
        Log.d(LOG_TAG, "Starting scan");
        bluetoothScanCallback = new android.bluetooth.le.ScanCallback() {
            public void onScanResult(int callbackType, ScanResult result) {

                Log.d(LOG_TAG, "onScanResult");

                ScanRecord record = result.getScanRecord();

                // if there is no record, discard this packet
                if (record == null) {
                    return;
                }

                byte[] id = record.getServiceData(TelemetryService.SERVICE_UUID);

                // if there is no data, discard
                if (id == null) {
                    return;
                }
                int rssi = result.getRssi();

                scanResultCallback.onScanResult(id, rssi);
            }
        };

        ScanSettings.Builder settingsBuilder = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            settingsBuilder.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                    .setNumOfMatches(ScanSettings.MATCH_NUM_MAX_ADVERTISEMENT)
                    .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            settingsBuilder.setLegacy(true);
        }

        /*ScanFilter filter = new ScanFilter.Builder()
                .setServiceUuid(DataCollectionService.SERVICE_UUID)
                .build();*/


        //bluetoothLeScanner.startScan(Collections.singletonList(filter), settingsBuilder.build(), bluetoothScanCallback);
        bluetoothLeScanner.startScan(bluetoothScanCallback);
    }

    public void stopScanning() {
        Log.d(LOG_TAG, "Stopping scanning");
        if (bluetoothScanCallback != null) {
            bluetoothLeScanner.stopScan(bluetoothScanCallback);
            bluetoothScanCallback = null;
        }
    }

    public interface ScanResultCallback {
        void onScanResult(byte[] id, int rssi);
    }
}
