package com.github.lurldgbodex.parsers;

import com.github.lurldgbodex.converters.ConverterRegistry;
import com.github.lurldgbodex.core.Converter;
import com.github.lurldgbodex.core.Parser;
import com.github.lurldgbodex.core.Validator;
import com.github.lurldgbodex.enums.Format;
import com.github.lurldgbodex.exceptions.ConverterException;
import com.github.lurldgbodex.exceptions.ParserException;
import com.github.lurldgbodex.mapper.Mapper;
import com.github.lurldgbodex.validation.RuleBasedValidator;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Parses CSV files into Java Objects.
 */
public class CsvParser implements Parser {

    private final Validator validator;
    private final Mapper mapper;
    private final ConverterRegistry converterRegistry;

    public CsvParser() {
        this.validator = new RuleBasedValidator();
        this.mapper = new Mapper();
        this.converterRegistry = new ConverterRegistry();
    }

    public CsvParser(Validator validator, Mapper mapper, ConverterRegistry registry) {
        this.validator = validator;
        this.mapper = mapper;
        this.converterRegistry = registry;
    }

    /**
     * Parses a CSV file into a list of objects.
     *
     * @param file The CSV file to parse
     * @param clazz The class of the objects to create.
     * @return A list of parsed objects.
     * @throws ParserException if parsing fails
     */
    @Override
    public <T> List<T> parseToObject(File file, Class<T> clazz) throws ParserException {
        List<T> result = new ArrayList<>();

        try (Reader reader = new FileReader(file);
            CSVParser csvParser = CSVFormat.Builder.create()
                    .setHeader().setSkipHeaderRecord(true).get().parse(reader)) {

            for (CSVRecord record : csvParser) {
                T obj = mapper.mapToObject(record, clazz, CSVRecord::get);
                validator.validate(obj);
                result.add(obj);
            }
            return result;
        } catch (Exception ex) {
            throw new ParserException("Failed to parse CSV file", ex);
        }
    }

    /**
     * Parses a CSV file into a list of maps.
     *
     * @param file The CSV file to parse
     * @return A list of parsed objects.
     * @throws ParserException if parsing fails
     */
    public List<Map<String, String>> parseToMaps(File file) throws ParserException {
        List<Map<String, String>> result = new ArrayList<>();

        try (Reader reader = new FileReader(file);
             CSVParser csvParser = CSVFormat.Builder.create().setHeader()
                     .setSkipHeaderRecord(true).get().parse(reader)) {

            for (CSVRecord record : csvParser) {
                result.add(record.toMap());
            }
            return result;
        } catch (Exception ex) {
            throw new ParserException("Failed to parse CSV file", ex);
        }
    }

    /**
     * Converts a CSV file into another Format
     *
     * @param input The CSV file to convert
     * @param format The format to convert to
     * @param output The converted file
     * @throws ConverterException if conversion fails
     */
    @Override
    public void convert(File input, Format format, File output) throws ConverterException {
        try {
            Converter converter = converterRegistry.getConverter(format);
            converter.convert(input, output);
        } catch (Exception ex) {
            throw new ConverterException("Conversion failed" + ex);
        }
    }

    /**
     * Converts a CSV file into another Format using pojo
     *
     * @param input The CSV file to convert
     * @param format The format to convert to
     * @param output The converted file
     * @throws ConverterException if conversion fails
     */
    @Override
    public <T> void convert(File input, Format format, File output, Class<T> clazz) {
        try {
            Converter converter = converterRegistry.getConverter(format);
            converter.convert(input, output, clazz);
        } catch (Exception ex) {
            throw new ConverterException("Conversion failed" + ex);
        }
    }
}
