package com.github.lurldgbodex.converters.csv;

import com.github.lurldgbodex.factory.ParserFactory;
import com.github.lurldgbodex.core.Converter;
import com.github.lurldgbodex.core.Parser;
import com.github.lurldgbodex.enums.Format;
import com.github.lurldgbodex.exceptions.ConverterException;
import com.github.lurldgbodex.util.JsonUtil;

import java.io.File;
import java.util.List;

public class CsvToJsonConverter implements Converter {

    private final JsonUtil jsonUtil;

    public CsvToJsonConverter() {
        this.jsonUtil = new JsonUtil();
    }


    @Override
    public Format getSourceFormat() {
        return Format.CSV;
    }

    @Override
    public Format getTargetFormat() {
        return Format.JSON;
    }

    @Override
    public <T> void convert(File input, File output, Class<T> clazz) throws ConverterException {
        try {
            Parser parser = ParserFactory.getParser(getSourceFormat());
            List<T> data = parser.parseToObject(input, clazz);
            jsonUtil.convertToJson(data, output);
        } catch (Exception ex) {
            throw new ConverterException("CSV to JSON conversion failed", ex);
        }
    }
}
