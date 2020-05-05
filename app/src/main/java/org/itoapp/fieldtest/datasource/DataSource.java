package org.itoapp.fieldtest.datasource;

import android.content.Context;

import java.lang.reflect.Type;

public interface DataSource {

    /**
     * Set up this datasource
     *
     * @param context the context to use
     * @return true if this datasource is supported, false if not
     */
    default boolean setup(Context context) {
        return true;
    }

    String[] getDataLabels();

    /**
     * Get the type of data returned
     *
     * @return A String or a primitive or an array of such
     */
    Type[] getDataTypes();

    /**
     * Query the datasource for the specific data.
     *
     * @return the data
     */
    Object[] getData();

    /**
     * Destroy this datasource. All claimed resources should be freed.
     */
    default void destroy() {
    }
}
