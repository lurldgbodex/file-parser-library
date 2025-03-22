package com.github.lurldgbodex.parsers;

import com.github.lurldgbodex.converters.ConverterRegistry;
import com.github.lurldgbodex.core.Converter;
import com.github.lurldgbodex.core.Validator;
import com.github.lurldgbodex.enums.Format;
import com.github.lurldgbodex.exceptions.ConverterException;
import com.github.lurldgbodex.exceptions.ParserException;
import com.github.lurldgbodex.exceptions.ValidatorException;
import com.github.lurldgbodex.mapper.Mapper;
import com.github.lurldgbodex.model.User;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CsvParserTest {
    private CsvParser csvParser;
    private Validator mockValidator;
    private Mapper mockMapper;
    private ConverterRegistry mockConverterRegistry;

    @BeforeEach
    void setup() {
        mockValidator = mock(Validator.class);
        mockMapper = mock(Mapper.class);
        mockConverterRegistry = mock(ConverterRegistry.class);
        csvParser = new CsvParser(mockValidator, mockMapper, mockConverterRegistry);
    }

    @Nested
    class ParseToObjectTests {

        @Test
        void parseToObject_validFile_ReturnsListOfObjects() {
            File file = new File("src/test/resources/csv/valid.csv");

            when(mockMapper.mapToObject(any(CSVRecord.class), eq(User.class), any()))
                    .thenReturn(new User("John Doe", "john@doe.com", false));

            List<User> result = csvParser.parseToObject(file, User.class);

            assertAll("Parsed objects",
                    () -> assertEquals(5, result.size()),
                    () -> assertEquals("John Doe", result.get(0).name()),
                    () -> assertEquals("john@doe.com", result.get(0).email()),
                    () -> assertFalse(result.get(0).active())
            );
        }

        @Test
        void parseToObject_invalidFile_throwsParseException() {
            File csvFile = new File("src/test/resources/csv/invalid.csv");

            when(mockMapper.mapToObject(any(), eq(User.class), any()))
                    .thenThrow(new ParserException("Failed to map data"));

            assertThrows(ParserException.class,
                    () -> csvParser.parseToObject(csvFile, User.class));
        }

        @Test
        void parseToObject_validationFails_throwsParseException() {
            File csvFile = new File("src/test/resources/csv/valid.csv");

            when(mockMapper.mapToObject(any(CSVRecord.class), eq(User.class), any()))
                    .thenReturn(new User("John Doe", "john@doe.com", true));
            doThrow(new ValidatorException("validation failed")).when(mockValidator).validate(any(User.class));

            assertThrows(ParserException.class,
                    () -> csvParser.parseToObject(csvFile, User.class));
        }
    }

    @Nested
    class ParseToMapsTests {
        @Test
        void parseToMaps_validFile_returnsListsOfMaps() {
            File csvFile = new File("src/test/resources/csv/valid.csv");

            List<Map<String, String>> result = csvParser.parseToMaps(csvFile);

            assertAll("Parsed Maps",
                    () -> assertEquals(5, result.size()),
                    () -> assertEquals("John Doe", result.get(0).get("name")),
                    () -> assertEquals("john@doe.com", result.get(0).get("email")),
                    () -> assertEquals("false", result.get(0).get("active"))
            );
        }

        @Test
        @Disabled
        void parseToMaps_invalidFile_throwsParseException() {
            File csvFile = new File("src/test/resources/csv/invalid.csv");

            assertThrows(ParserException.class,
                    () -> csvParser.parseToMaps(csvFile));
        }
    }

    @Nested
    class ConvertTests {
        @Test
        void convert_validFile_callsConverter(@TempDir Path tempDir) {
            File inputFile = new File("src/test/resources/csv/valid.csv");
            File outputFile = tempDir.resolve("output.json").toFile();

            Converter mockConverter = mock(Converter.class);
            when(mockConverterRegistry.getConverter(Format.JSON)).thenReturn(mockConverter);

            csvParser.convert(inputFile, Format.JSON, outputFile);
            verify(mockConverter, times(1)).convert(inputFile, outputFile);
        }

        @Test
        void convert_invalidFormat_throwsConverterException(@TempDir Path tempDir) {
            File inputFile = new File("src/test/resources/csv/valid.csv");
            File outputFile = tempDir.resolve("output.json").toFile();

            when(mockConverterRegistry.getConverter(Format.JSON)).thenReturn(null);

            assertThrows(ConverterException.class,
                    () -> csvParser.convert(inputFile, Format.JSON, outputFile));
        }
    }

    @Nested
    class ConvertWithPojoTests {
        @Test
        void convertWithPojo_validFile_callsConverter(@TempDir Path tempDir) {
            File inputFile = new File("src/test/resources/csv/valid.csv");
            File outputFile = tempDir.resolve("output.json").toFile();

            Converter mockConverter = mock(Converter.class);
            when(mockConverterRegistry.getConverter(Format.JSON)).thenReturn(mockConverter);

            csvParser.convert(inputFile, Format.JSON, outputFile, User.class);

            verify(mockConverter, times(1)).convert(inputFile, outputFile, User.class);
        }

        @Test
        void convertWithPojo_invalidFormat_throwsConverterException(@TempDir Path tempDir) {
            File inputFile = new File("src/test/resources/csv/valid.csv");
            File outputFile = tempDir.resolve("output.json").toFile();

            when(mockConverterRegistry.getConverter(Format.JSON)).thenReturn(null);

            assertThrows(ConverterException.class,
                    () -> csvParser.convert(inputFile, Format.JSON, outputFile, User.class));
        }
    }
}