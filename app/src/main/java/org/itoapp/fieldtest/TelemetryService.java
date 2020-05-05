package org.itoapp.fieldtest;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.ParcelUuid;

import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class TelemetryService extends Service {
    public static final ParcelUuid SERVICE_UUID = ParcelUuid.fromString("000063a1-0000-1000-8000-00805f9b34fb");
    public static final byte[] SERVICE_DATA = new byte[13];
    private static final String DEFAULT_NOTIFICATION_CHANNEL = "ContactTracing";
    private static final int NOTIFICATION_ID = 1;
    private static final File STORAGE_DIRECTORY = new File("/storage/self/primary/ito-data");

    static {
        new Random().nextBytes(SERVICE_DATA);
    }

    private Handler serviceHandler;
    private BleScanner bleScanner;
    private BleAdvertiser bleAdvertiser;
    private File logFile;
    private CsvWriter csvWriter;
    private DataLogger dataLogger;

    @Override
    public void onCreate() {
        super.onCreate();

        STORAGE_DIRECTORY.mkdirs();

        if (!STORAGE_DIRECTORY.isDirectory()) {
            throw new RuntimeException("Storage directory does not exist!");
        }

        {
            int logFileIndex = 1;
            do {
                logFile = new File(STORAGE_DIRECTORY, logFileIndex++ + ".csv");
            } while (logFile.exists());
        }

        try {
            csvWriter = new CsvWriter(logFile);
            dataLogger = new DataLogger(this, csvWriter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        HandlerThread handlerThread = new HandlerThread("DataCollectionThread");
        handlerThread.start();

        serviceHandler = new Handler(handlerThread.getLooper());
        startBluetooth();
    }

    private void stopBluetooth() {
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

        bleScanner = null;
        bleAdvertiser = null;
    }

    private void startBluetooth() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        assert bluetoothManager != null;
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        bleScanner = new BleScanner(bluetoothAdapter, (id, rssi) -> dataLogger.logData(id, rssi));
        bleAdvertiser = new BleAdvertiser(bluetoothAdapter, serviceHandler);

        bleAdvertiser.startAdvertising();
        bleScanner.startScanning();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        runAsForgroundService();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopBluetooth();

        dataLogger.destroy();

        super.onDestroy();
    }

    @TargetApi(26)
    private void createNotificationChannel(NotificationManager notificationManager) {
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel mChannel = new NotificationChannel(DEFAULT_NOTIFICATION_CHANNEL, DEFAULT_NOTIFICATION_CHANNEL, importance);
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        mChannel.setImportance(NotificationManager.IMPORTANCE_LOW);
        notificationManager.createNotificationChannel(mChannel);
    }

    private void runAsForgroundService() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            createNotificationChannel(notificationManager);
        }

        Intent notificationIntent = new Intent();

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this,
                DEFAULT_NOTIFICATION_CHANNEL)
                .setContentIntent(pendingIntent)
                .setVibrate(null)
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
