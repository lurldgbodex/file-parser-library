package com.github.lurldgbodex.core;

import com.github.lurldgbodex.enums.Format;
import com.github.lurldgbodex.exceptions.ConverterException;
import com.github.lurldgbodex.exceptions.ParserException;

import java.io.File;
import java.util.List;

public interface Parser {
    /**
     * Parses a file to an object of the provided class type
     *
     * @param file The file to parse
     * @param clazz The class of the object to create
     * @return List of parsed object
     * @throws ParserException if file parsing fails
     */
    <T> List<T> parseToObject(File file, Class<T> clazz) throws ParserException;

    /**
     * Converts a file from one format to another format
     *
     * @param inputFile The file to convert
     * @param outputFormat The format to convert to
     * @param outputFile The converted file
     * @throws ConverterException if file conversion fails
     */
    void convert(File inputFile, Format outputFormat, File outputFile) throws ConverterException;

    /**
     * converts a file form one format to another using Java POJO enabling validation
     *
     * @param inputFile File to convert
     * @param outputFormat format to convert to
     * @param outputFile converted file
     * @param clazz Java POJO class to use
     * @throws ConverterException if conversion fails
     */
    <T> void convert(File inputFile, Format outputFormat, File outputFile, Class<T> clazz) throws ConverterException;
}
