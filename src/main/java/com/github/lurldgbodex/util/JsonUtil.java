package com.github.lurldgbodex.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JsonUtil {

    private final ObjectMapper jsonMapper;

    public JsonUtil() {
        this.jsonMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }

    public <T> void convertToJson(List<T> data, File output) throws IOException {
        jsonMapper.writeValue(output, data);
    }
}
