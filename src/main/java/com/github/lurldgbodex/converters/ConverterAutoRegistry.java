package com.github.lurldgbodex.converters;

import com.github.lurldgbodex.core.Converter;
import com.github.lurldgbodex.enums.Format;
import com.github.lurldgbodex.exceptions.ConverterException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ConverterRegistry {
    private final Map<Format, Converter> converters = new HashMap<>();

    public void registerConverter(Format format, Converter converter) {
        converters.put(format, converter);
    }

    public Converter getConverter(Format format) {
        return Optional.ofNullable(converters.get(format))
                .orElseThrow(() -> new ConverterException("No converter found for format: " + format));
    }

    public boolean supportsPojoConversion(Format format) {
        return converters.containsKey(format);
    }
}
