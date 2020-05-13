package org.itoapp.fieldtest.datasource;

import java.lang.reflect.Type;

public class Timestamp implements DataSource {
    @Override
    public String[] getDataLabels() {
        return new String[]{"unix_time_millis"};
    }

    @Override
    public Type[] getDataTypes() {
        return new Type[]{long.class};
    }

    @Override
    public Object[] getData() {
        return new Object[]{System.currentTimeMillis()};
    }
}
