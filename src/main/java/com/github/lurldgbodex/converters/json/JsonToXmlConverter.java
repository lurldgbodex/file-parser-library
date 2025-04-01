package com.github.lurldgbodex.converters.json;

import com.github.lurldgbodex.factory.ParserFactory;
import com.github.lurldgbodex.core.Converter;
import com.github.lurldgbodex.core.Parser;
import com.github.lurldgbodex.enums.Format;
import com.github.lurldgbodex.exceptions.ConverterException;
import com.github.lurldgbodex.util.XmlUtil;

import java.io.File;
import java.util.List;

public class JsonToXmlConverter implements Converter {

    private final XmlUtil xmlUtil;

    public JsonToXmlConverter() {
        this.xmlUtil = new XmlUtil();
    }

    @Override
    public Format getSourceFormat() {
        return Format.JSON;
    }

    @Override
    public Format getTargetFormat() {
        return Format.XML;
    }

    @Override
    public <T> void convert(File input, File output, Class<T> clazz) throws ConverterException {
        try {
            Parser parser = ParserFactory.getParser(getSourceFormat());
            List<T> data = parser.parseToObject(input, clazz);
            xmlUtil.convertToXml(data, output);
        } catch (Exception ex) {
            throw new ConverterException("Failed to convert Json to Xml", ex);
        }
    }
}
