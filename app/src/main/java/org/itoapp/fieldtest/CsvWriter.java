package org.itoapp.fieldtest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;

class CsvWriter implements AutoCloseable, Flushable {
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private Writer writer;

    public CsvWriter(File logFile) throws IOException {
        writer = new BufferedWriter(new FileWriter(logFile, true));
    }

    public void writeLine(Object[] fields, Type[] types) throws IOException {
        for (int i = 0; i < fields.length; i++) {
            Object value = fields[i];
            Type type = types[i];
            boolean isLast = i == fields.length - 1;

            if (value == null) {
                writer.write("\"\"");
            } else if (type == String.class) {
                writer.write('"');
                writer.write((String) value);
                writer.write('"');
            } else if (type == byte[].class) {
                for (byte b : (byte[]) value) {
                    writer.write(HEX_DIGITS[(0xF0 & b) >>> 4]);
                    writer.write(HEX_DIGITS[0x0F & b]);
                }
            } else {
                writer.write(value.toString());
            }

            writer.write(isLast ? "\n" : ", ");
        }
    }

    @Override
    public void close() throws Exception {
        if (writer != null)
            writer.close();
    }

    @Override
    public void flush() throws IOException {
        writer.flush();
    }
}
