package com.github.lurldgbodex.core;

import com.github.lurldgbodex.enums.Format;
import com.github.lurldgbodex.exceptions.ConverterException;

import java.io.File;

public interface Converter {

    Format getSourceFormat();
    Format getTargetFormat();
    <T> void convert(File input, File output, Class<T> clazz) throws ConverterException;
}
