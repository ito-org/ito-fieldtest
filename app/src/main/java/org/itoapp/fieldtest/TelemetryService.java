package org.itoapp.fieldtest;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.ParcelUuid;

import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class TelemetryService extends Service {
    public static final ParcelUuid SERVICE_UUID = ParcelUuid.fromString("00003098-0000-1000-8000-00805f9b34fb");
    public static final byte[] BROADCAST_ID = new byte[13];
    private static final String DEFAULT_NOTIFICATION_CHANNEL = "ContactTracing";
    private static final int NOTIFICATION_ID = 1;

    static {
        new Random().nextBytes(BROADCAST_ID);
    }

    private DataLogger dataLogger;

    @Override
    public void onCreate() {
        super.onCreate();

        File storageDirectory = new File(Environment.getExternalStorageDirectory(), "ito-data");
        storageDirectory.mkdirs();

        if (!storageDirectory.isDirectory()) {
            throw new RuntimeException("Storage directory does not exist!");
        }

        File logFolder;
        {
            int folderIndex = 1;
            do {
                logFolder = new File(storageDirectory, folderIndex++ + "");
            } while (logFolder.exists());
        }

        try {
            logFolder.mkdir();
            dataLogger = new DataLogger(this, logFolder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        runAsForgroundService();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
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
