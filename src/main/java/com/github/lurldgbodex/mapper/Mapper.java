package com.github.lurldgbodex.mapper;

import com.github.lurldgbodex.annotations.FieldMapping;
import com.github.lurldgbodex.exceptions.ParserException;

import java.lang.reflect.Field;
import java.util.function.BiFunction;

public class Mapper {

    public <T, D> T mapToobject(D data, Class<T> clazz, BiFunction<D, String, String> valueExtractor) {
        try {
            T obj = clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                FieldMapping annotation = field.getAnnotation(FieldMapping.class);

                if (annotation != null) {
                    String column = annotation.column();
                    String value = valueExtractor.apply(data, column);
                    if (value == null || value.isEmpty()) continue;

                    field.setAccessible(true);
                    setFieldValue(field, obj, value);
                }
            }
            return obj;
        } catch (Exception ex) {
            throw new ParserException("Failed to map data to object", ex);
        }
    }

    private void setFieldValue(Field field, Object target, String value) {
        Class<?> type = field.getType();
        try {
            if (type == String.class) {
                field.set(target, value);
            } else if (type == int.class || type == Integer.class) {
                field.set(target, Integer.parseInt(value));
            } else if (type == double.class || type == Double.class) {
                field.set(target, Double.parseDouble(value));
            } else if (type == boolean.class || type == Boolean.class) {
                field.set(target, Boolean.parseBoolean(value));
            }
        } catch (IllegalAccessException ex) {
            throw new ParserException("Error setting field value", ex);
        } catch (NumberFormatException nfe) {
            throw new ParserException("Invalid numeric format for field: " + field.getName(), nfe);
        }
    }
}
