package com.github.lurldgbodex.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class XmlUtil {

    private final ObjectMapper xmlMapper;

    public XmlUtil() {
        this.xmlMapper = new XmlMapper();
    }

    public <T> void convertToXml(List<T> data, File output) throws IOException {
        xmlMapper.writeValue(output, data);
    }
}
