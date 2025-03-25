package com.github.lurldgbodex.util;

import com.github.lurldgbodex.exceptions.ConverterException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

public class CsvUtil {

    public static <T> void convertToCsv(List<T> data, File output) throws IOException {
        try (FileWriter writer = new FileWriter(output); CSVPrinter csvPrinter = new CSVPrinter(
                writer, CSVFormat.RFC4180.builder().setHeader(getHeaders(data.get(0))).get())) {

            for (T obj : data) {
                csvPrinter.printRecord(getValues(obj));
            }
        }
    }

    private static <T> String[] getHeaders(T obj) {
        return java.util.Arrays.stream(obj.getClass().getDeclaredFields())
                .map(Field::getName)
                .toArray(String[]::new);
    }

    private static <T> List<Object> getValues(T obj) {
        return java.util.Arrays.stream(obj.getClass().getDeclaredFields())
                .map(field -> {
                    field.setAccessible(true);
                    try {
                        return field.get(obj);
                    } catch (IllegalAccessException ex) {
                        throw new ConverterException("Failed to convert to csv", ex);
                    }
                }).toList();
    }
}
