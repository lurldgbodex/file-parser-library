package com.github.lurldgbodex.parsers;

import com.github.lurldgbodex.converters.ConverterAutoRegistry;
import com.github.lurldgbodex.core.Converter;
import com.github.lurldgbodex.core.Parser;
import com.github.lurldgbodex.core.Validator;
import com.github.lurldgbodex.enums.Format;
import com.github.lurldgbodex.exceptions.ConverterException;
import com.github.lurldgbodex.exceptions.ParserException;
import com.github.lurldgbodex.mapper.Mapper;
import com.github.lurldgbodex.validation.RuleBasedValidator;

import java.io.File;
import java.util.List;

/**
 * Parses JSON files into java objects
 */
public class JsonParser implements Parser {

    private final Mapper mapper;

    public JsonParser() {
        ConverterAutoRegistry.init();
        this.mapper = new Mapper();
    }

    /**
     * Parses a JSON file into a list of objects
     *
     * @param file The JSON file to parse
     * @param clazz The class of the objects to create
     * @return A list of parsed objects
     * @throws ParserException if parsing fails
     */
    @Override
    public <T> List<T> parseToObject(File file, Class<T> clazz) throws ParserException {
        Validator validator = new RuleBasedValidator();
        try {
            List<T> objects = mapper.mapToObject(file, clazz, false);

            for (T obj : objects) {
                validator.validate(obj);
            }
            return objects;
        } catch (Exception ex) {
            throw  new ParserException("Failed to parse JSON file", ex);
        }
    }


    /**
     * converts a json file to another format using POJO
     *
     * @param inputFile File to convert
     * @param outputFormat format to convert to
     * @param outputFile converted file
     * @param clazz Java POJO class to use
     * @param <T> class type
     * @throws ConverterException if conversion fails
     */
    @Override
    public <T> void convert(File inputFile, Format outputFormat, File outputFile, Class<T> clazz) throws ConverterException {
        Converter converter = ConverterAutoRegistry.INSTANCE
                .getConverter(Format.JSON, outputFormat);
        converter.convert(inputFile, outputFile, clazz);
    }
}
