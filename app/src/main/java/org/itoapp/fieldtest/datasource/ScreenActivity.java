package org.itoapp.fieldtest.datasource;

import android.content.Context;
import android.os.Handler;
import android.os.PowerManager;

import java.lang.reflect.Type;

import static android.content.Context.POWER_SERVICE;

public class ScreenActivity implements DataSource {
    private static final int POLLING_INTERVAL = 100;

    private Handler handler;
    private PowerManager powerManager;
    private DataListener dataListener;

    private boolean wasScreenOn;

    private Runnable checkScreen = () -> {
        if (isScreenOn() ^ wasScreenOn) {
            wasScreenOn = isScreenOn();
            if (dataListener != null)
                dataListener.onDataReceived(new Object[]{isScreenOn()});
        }
        handler.postDelayed(this.checkScreen, POLLING_INTERVAL);
    };

    private boolean isScreenOn() {
        return powerManager.isInteractive();
    }

    @Override
    public boolean setup(Context context) {
        powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
        handler = new Handler();
        handler.post(checkScreen);
        return powerManager != null;
    }

    @Override
    public String[] getDataLabels() {
        return new String[]{"screen_is_interactive"};
    }

    @Override
    public Type[] getDataTypes() {
        return new Type[]{boolean.class};
    }


    @Override
    public void setDataListener(DataListener listener) {
        this.dataListener = listener;
        dataListener.onDataReceived(new Object[]{isScreenOn()});
    }

    @Override
    public void destroy() {
        this.dataListener = null;
        handler.removeCallbacks(checkScreen);
    }
}
