package com.github.lurldgbodex.mapper;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.lurldgbodex.annotations.FieldMapping;
import com.github.lurldgbodex.exceptions.ParserException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public class Mapper {
    private final ObjectMapper jsonMapper;
    private final XmlMapper xmlMapper;

    private static final Map<Class<?>, List<FieldInfo>> fieldCache = new ConcurrentHashMap<>();

    public Mapper() {
        this.jsonMapper = createConfiguredObjectMapper(new ObjectMapper());
        this.xmlMapper = (XmlMapper) createConfiguredObjectMapper(new XmlMapper());
    }


    /**
     * Maps a file (json or xml) to a list of objects.
     *
     * @param file The file to parse.
     * @param clazz the target class.
     * @param isXml whether the file is XML (true) or JSON (false).
     * @return A list of mapped objects
     * @throws ParserException if parsing fails
     */
    public <T> List<T> mapToObject(File file, Class<T> clazz, boolean isXml) throws ParserException {
        try {
            ObjectMapper mapper = isXml ? xmlMapper : jsonMapper;
            return mapper.readValue(file, mapper.getTypeFactory()
                    .constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            throw new ParserException("Failed to parse file", e);
        }
    }

    /**
     * Maps a data source (e.g CSV record) to an object using a value extractor.
     *
     * @param data The data source (e.g., CSVRecord).
     * @param clazz The target class.
     * @param valueExtractor A function to extract values from the data source.
     * @return A mapped object.
     * @throws ParserException if mapping fails
     */
    public <T, D> T mapToObject(D data, Class<T> clazz, BiFunction<D, String, String> valueExtractor) throws ParserException {
        try {
            T obj = createInstance(clazz);

            for (FieldInfo fieldInfo : getFieldInfo(clazz)) {
                String column = fieldInfo.column();
                String value = valueExtractor.apply(data, column);

                if (value != null && !value.isEmpty()) {
                    setFieldValue(fieldInfo.field(), obj, value, column);
                } else if (isNestedObject(fieldInfo.field())) {
                    Object nestedObj = mapToObject(data, fieldInfo.field().getType(),
                            (d, subColumn) -> valueExtractor.apply(d, column + "." + subColumn));
                    fieldInfo.field().set(obj, nestedObj);
                }
            }
            return obj;
        } catch (Exception ex) {
            throw new ParserException("Failed to map data to object", ex);
        }
    }

    /**
     * sets the value of a field on the target object.
     */
    private void setFieldValue(Field field, Object target, String value, String fullPath) {
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
            } else if (type == long.class || type == Long.class) {
                field.set(target, Long.parseLong(value));
            } else if (type == LocalDate.class) {
                field.set(target, LocalDate.parse(value));
            } else if (type.isEnum()) {
                field.set(target, Enum.valueOf((Class<Enum>) type, value));
            } else {
                String[] pathParts = fullPath.split("\\.");
                String nestedFieldName = pathParts[0];

                for (String path : pathParts) {
                    System.out.println("Path Parts: " + path);
                }

                Field nestedField = field.getType().getDeclaredField(nestedFieldName);
                nestedField.setAccessible(true);

                Object nestedObj = field.get(target);
                if (nestedObj == null) {
                    nestedObj = createInstance(nestedField.getType());
                    field.set(target, nestedObj);
                }

                String remainingPath = String.join(".", Arrays.copyOfRange(pathParts, 1, pathParts.length));

                if (!remainingPath.isEmpty()) {
                    Field actualNestedField = nestedObj.getClass().getDeclaredField(remainingPath);
                    actualNestedField.setAccessible(true);
                    setFieldValue(actualNestedField, nestedObj, value, remainingPath);
                }

            }
        } catch (IllegalAccessException ex) {
            throw new ParserException("Error setting field value", ex);
        } catch (NumberFormatException nfe) {
            throw new ParserException("Invalid numeric format for field: " + field.getName(), nfe);
        } catch (Exception ex) {
            throw new ParserException("Failed to create nested object", ex);
        }
    }

    private boolean isNestedObject(Field field) {
        Class<?> type = field.getType();
        return !type.isPrimitive() && !type.equals(String.class) && !type.equals(Integer.class) &&
                !type.equals(Double.class) && !type.equals(Boolean.class) && !type.equals(Long.class) &&
                !type.equals(LocalDate.class) && !type.isEnum();
    }

    private List<FieldInfo> getFieldInfo(Class<?> clazz) {
        return fieldCache.computeIfAbsent(clazz, k -> {
            List<FieldInfo> fieldInfo = new ArrayList<>();

            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);

                FieldMapping annotation = field.getAnnotation(FieldMapping.class);
                String columnName = (annotation != null) ? annotation.column() : field.getName();

                fieldInfo.add(new FieldInfo(field, columnName.toLowerCase()));
            }
            return fieldInfo;
        });
    }


    /**
     * creates an instance of the target class using a no-arg constructor or @JsonCreator
     */
    private <T> T createInstance(Class<T> clazz) throws Exception {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException ex) {
            for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
                if (constructor.isAnnotationPresent(JsonCreator.class)) {
                    constructor.setAccessible(true);

                    Class<?>[] paramTypes = constructor.getParameterTypes();
                    Object[] defaultArgs = new Object[paramTypes.length];

                    for (int i = 0; i < paramTypes.length; i++) {
                        defaultArgs[i] = getDefaultValue(paramTypes[i]);
                    }

                    Object instance = constructor.newInstance(defaultArgs);
                    return clazz.cast(instance);
                }
            }
            throw new ParserException("No suitable constructor found for class: " + clazz.getName());
        }
    }

    private Object getDefaultValue(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            if (clazz == boolean.class) return false;
            if (clazz == char.class) return '\u0000';
            if (clazz == byte.class || clazz == short.class
                    || clazz == int.class || clazz == long.class) return 0;
            if (clazz == float.class || clazz == double.class) return 0.0;
        }

        return null;
    }

    private ObjectMapper createConfiguredObjectMapper(ObjectMapper mapper) {
        return mapper.registerModules(new JavaTimeModule(), new SimpleModule())
                .setAnnotationIntrospector(new FieldMappingAnnotationIntrospector())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * Custom annotation introspector for FieldMapping.
     */
    private static class FieldMappingAnnotationIntrospector extends JacksonAnnotationIntrospector {
        @Override
        public PropertyName findNameForSerialization(Annotated annotated) {
            FieldMapping annotation = annotated.getAnnotation(FieldMapping.class);
            return annotation != null
                    ? PropertyName.construct(annotation.column())
                    : super.findNameForSerialization(annotated);
        }

        @Override
        public PropertyName findNameForDeserialization(Annotated annotated) {
            FieldMapping ann = annotated.getAnnotation(FieldMapping.class);
            return ann != null
                    ? PropertyName.construct(ann.column())
                    : super.findNameForDeserialization(annotated);
        }
    }

    /**
     * Inner class to hold field information.
     */
    private record FieldInfo(Field field, String column) {
        private FieldInfo(Field field, String column) {
            this.field = field;
            this.column = column.toLowerCase();
        }
    }
}
