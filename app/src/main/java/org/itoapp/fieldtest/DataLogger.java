package org.itoapp.fieldtest;

import android.content.Context;

import org.itoapp.fieldtest.datasource.DataSource;
import org.itoapp.fieldtest.datasource.DeviceModel;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataLogger {

    private List<DataSource> dataSources = new ArrayList<>(Arrays.asList(new DeviceModel()));
    private CsvWriter csvWriter;
    private Object[] values;
    private Type[] valueTypes;


    public DataLogger(Context context, CsvWriter csvWriter) throws IOException {
        this.csvWriter = csvWriter;

        List<String> valueLabels = new ArrayList<>();
        {
            List<Type> valueTypes = new ArrayList<>();

            // add rssi and id values
            valueLabels.add("id");
            valueTypes.add(byte[].class);

            valueLabels.add("rssi");
            valueTypes.add(int.class);


            for (int i = 0; i < dataSources.size(); i++) {
                DataSource dataSource = dataSources.get(i);
                if (!dataSource.setup(context)) {
                    dataSources.remove(i);
                    i--;
                    continue;
                }

                String[] sourceLabels = dataSource.getDataLabels();
                Type[] sourceTypes = dataSource.getDataTypes();
                if (sourceLabels.length != sourceTypes.length) {
                    throw new RuntimeException("Label and type lenghts don't match!");
                }

                valueLabels.addAll(Arrays.asList(sourceLabels));
                valueTypes.addAll(Arrays.asList(sourceTypes));
            }

            this.valueTypes = valueTypes.toArray(new Type[0]);
        }

        values = new Object[this.valueTypes.length];

        // Write the value labels (rssi, id…)
        Type[] untypedValues = new Type[this.valueTypes.length];
        csvWriter.writeLine(valueLabels.toArray(), untypedValues);

        // Write type information in a second line
        String[] typeNames = new String[this.valueTypes.length];
        for (int i = 0; i < typeNames.length; i++) {
            Type type = this.valueTypes[i];
            if (type == String.class)
                typeNames[i] = "string";
            else if (type instanceof Class) {
                typeNames[i] = ((Class) type).getCanonicalName();
            }
            else
                typeNames[i] = type.toString();
        }
        csvWriter.writeLine(typeNames, untypedValues);
        csvWriter.flush();
    }

    public void logData(byte[] id, int rssi) {
        int i = 0;
        values[i++] = id;
        values[i++] = rssi;

        for (DataSource dataSource : dataSources)
            for (Object value : dataSource.getData())
                values[i++] = value;

        try {
            csvWriter.writeLine(values, valueTypes);
            csvWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void destroy() {
        for (DataSource dataSource : dataSources) {
            dataSource.destroy();
        }
    }
}
