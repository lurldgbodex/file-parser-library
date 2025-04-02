package com.github.lurldgbodex.factory;

import com.github.lurldgbodex.core.Parser;
import com.github.lurldgbodex.enums.Format;
import com.github.lurldgbodex.parsers.CsvParser;
import com.github.lurldgbodex.parsers.JsonParser;
import com.github.lurldgbodex.parsers.XmlParser;

public class ParserFactory {

    public static Parser getParser(Format format) {
        return switch (format) {
            case CSV -> new CsvParser();
            case JSON -> new JsonParser();
            case XML -> new XmlParser();
        };
    }
}
