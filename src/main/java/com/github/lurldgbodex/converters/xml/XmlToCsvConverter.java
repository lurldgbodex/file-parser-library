package com.github.lurldgbodex.converters.xml;

import com.github.lurldgbodex.factory.ParserFactory;
import com.github.lurldgbodex.core.Converter;
import com.github.lurldgbodex.core.Parser;
import com.github.lurldgbodex.enums.Format;
import com.github.lurldgbodex.exceptions.ConverterException;
import com.github.lurldgbodex.util.CsvUtil;

import java.io.File;
import java.util.List;

public class XmlToCsvConverter implements Converter {

    @Override
    public Format getSourceFormat() {
        return Format.XML;
    }

    @Override
    public Format getTargetFormat() {
        return Format.CSV;
    }

    @Override
    public <T> void convert(File input, File output, Class<T> clazz) throws ConverterException {
        try {
            Parser xmlParser = ParserFactory.getParser(getSourceFormat());
            List<T> data = xmlParser.parseToObject(input, clazz);
            CsvUtil.convertToCsv(data, output);
        } catch (Exception e) {
            throw new ConverterException("Failed to convert XML to CSV", e);
        }
    }
}
