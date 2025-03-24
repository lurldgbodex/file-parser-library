package com.github.lurldgbodex.converters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.lurldgbodex.core.Converter;
import com.github.lurldgbodex.exceptions.ConverterException;
import com.github.lurldgbodex.parsers.CsvParser;

import java.io.File;
import java.util.List;

public class CsvToJsonConverter implements Converter {
    private final CsvParser csvParser;
    private final ObjectMapper jsonMapper;

    public CsvToJsonConverter() {
        this.csvParser = new CsvParser();
        this.jsonMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }


    @Override
    public <T> void convert(File input, File output, Class<T> clazz) throws ConverterException {
        try {
            List<T> data = csvParser.parseToObject(input, clazz);
            jsonMapper.writeValue(output, data);
        } catch (Exception ex) {
            throw new ConverterException("CSV to JSON conversion failed", ex);
        }
    }
}
