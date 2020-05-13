package org.itoapp.fieldtest.datasource;

import android.content.Context;
import android.os.PowerManager;

import java.lang.reflect.Type;

import static android.content.Context.POWER_SERVICE;

public class ScreenActivity implements DataSource {
    private PowerManager powerManager;

    @Override
    public boolean setup(Context context) {
        powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
        return powerManager != null;
    }

    @Override
    public String[] getDataLabels() {
        return new String[]{"screen_is_interactive"};
    }

    @Override
    public Type[] getDataTypes() {
        return new Type[]{boolean.class, boolean.class};
    }

    @Override
    public Object[] getData() {
        return new Object[]{powerManager.isInteractive()};
    }
}
