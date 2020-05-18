package org.itoapp.fieldtest;

import android.content.Context;

import org.itoapp.fieldtest.datasource.Accelerometer;
import org.itoapp.fieldtest.datasource.AmbientTemperature;
import org.itoapp.fieldtest.datasource.BLE;
import org.itoapp.fieldtest.datasource.DataSource;
import org.itoapp.fieldtest.datasource.DeviceInformation;
import org.itoapp.fieldtest.datasource.GPS;
import org.itoapp.fieldtest.datasource.Gyroscope;
import org.itoapp.fieldtest.datasource.MagneticField;
import org.itoapp.fieldtest.datasource.Proximity;
import org.itoapp.fieldtest.datasource.ScreenActivity;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataLogger {

    private final List<DataSource> dataSources = Arrays.asList(
            // BLE and DeviceInformation is the absolute minimum
            // It would only allow recording bluetooth contacts and their RSSI values of time
            new BLE(),
            new DeviceInformation(),

            new Accelerometer(),
            new Gyroscope(),
            new MagneticField(),
            new GPS(),
            new Proximity(),
            new ScreenActivity(),
            new AmbientTemperature());

    private Map<DataSource, CsvWriter> dataSourceCsvWriters = new HashMap<>();

    public DataLogger(Context context, File outputFolder) throws IOException {
        for (DataSource dataSource : dataSources) {
            if (!dataSource.setup(context)) {
                continue;
            }

            String className = dataSource.getClass().getSimpleName();
            File csvFileName = new File(outputFolder, className + ".csv");

            CsvWriter csvWriter = new CsvWriter(csvFileName);
            dataSourceCsvWriters.put(dataSource, csvWriter);

            String[] fieldNames = dataSource.getDataLabels();
            Type[] fieldTypes = dataSource.getDataTypes();

            // Append the timestamp field to each line
            fieldNames = Arrays.copyOf(fieldNames, fieldNames.length + 1);
            fieldNames[fieldNames.length - 1] = "timestamp";
            fieldTypes = Arrays.copyOf(fieldTypes, fieldTypes.length + 1);
            fieldTypes[fieldTypes.length - 1] = long.class;

            if (fieldNames.length != fieldTypes.length) throw new AssertionError();

            Type[] untypedValues = new Type[fieldNames.length];

            // Write the value labels (rssi, id…)
            csvWriter.writeLine(fieldNames, untypedValues);

            // Write type information in a second line
            String[] typeNames = new String[fieldTypes.length];
            for (int i = 0; i < typeNames.length; i++) {
                Type type = fieldTypes[i];
                if (type == String.class)
                    typeNames[i] = "string";
                else if (type instanceof Class) {
                    typeNames[i] = ((Class) type).getCanonicalName();
                } else
                    typeNames[i] = type.toString();
            }
            csvWriter.writeLine(typeNames, untypedValues);
            csvWriter.flush();

            Type[] finalFieldTypes = fieldTypes; // "effective-final" smh f*** java
            dataSource.setDataListener(data -> {
                try {
                    Object[] dataWithTimestamp = Arrays.copyOf(data, data.length + 1);
                    dataWithTimestamp[dataWithTimestamp.length - 1] = System.currentTimeMillis();
                    csvWriter.writeLine(dataWithTimestamp, finalFieldTypes);
                    csvWriter.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public void destroy() {
        for (Map.Entry<DataSource, CsvWriter> dataSourceEntry : dataSourceCsvWriters.entrySet()) {
            dataSourceEntry.getKey().destroy();
            try {
                dataSourceEntry.getValue().close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
