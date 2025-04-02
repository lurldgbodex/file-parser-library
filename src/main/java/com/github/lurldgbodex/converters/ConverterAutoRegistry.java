package com.github.lurldgbodex.converters;

import com.github.lurldgbodex.converters.dto.ConverterKey;
import com.github.lurldgbodex.core.Converter;
import com.github.lurldgbodex.enums.Format;
import com.github.lurldgbodex.exceptions.ConverterException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;

public enum ConverterAutoRegistry {
    INSTANCE;
    private final Map<ConverterKey, Converter> converters = new HashMap<>();

    public void register(Converter converter) {
        ConverterKey key = new ConverterKey(
                converter.getSourceFormat(), converter.getTargetFormat());
        converters.put(key, converter);
    }

    public Converter getConverter(Format source, Format target) {
        ConverterKey key = new ConverterKey(source, target);
        return Optional.ofNullable(converters.get(key))
                .orElseThrow(() -> new ConverterException(
                        "No converter found for: " + source + " to " + target));
    }

    public static void init() {
        ServiceLoader.load(Converter.class)
                .forEach(INSTANCE::register);
    }
}
