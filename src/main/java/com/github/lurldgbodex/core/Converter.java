package com.github.lurldgbodex.core;

import com.github.lurldgbodex.exceptions.ConverterException;

import java.io.File;

public interface Converter {

    void convert(File input, File output) throws ConverterException;

    <T> void convert(File input, File output, Class<T> clazz) throws ConverterException;
}
