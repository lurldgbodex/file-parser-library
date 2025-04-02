package com.github.lurldgbodex.parsers;

import com.github.lurldgbodex.core.Parser;
import com.github.lurldgbodex.exceptions.ParserException;
import com.github.lurldgbodex.model.Department;
import com.github.lurldgbodex.model.Employee;
import com.github.lurldgbodex.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvParserTest {
    private static final String TEST_RESOURCES = "src/test/resources/csv/";
    private Parser csvParser;

    @BeforeEach
    void setup() {
        csvParser = new CsvParser();
    }

    @Nested
    class TestParseToObject {

        @Test
        void testParseToObject_validCsvFile() {
            File csvFile = new File(TEST_RESOURCES + "valid.csv");

            List<User> result = csvParser.parseToObject(csvFile, User.class);

            assertAll("User Properties",
                    () -> assertEquals(5, result.size()),
                    () -> assertEquals("Michael Smith", result.get(1).getName()),
                    () -> assertEquals("michael@smith.com", result.get(1).getEmail()),
                    () -> assertTrue(result.get(1).isActive())
            );
        }

        @Test
        void testParseToObject_invalidCsvFile() {
            File csvFile = new File(TEST_RESOURCES + "invalid.csv");
            assertThrows(ParserException.class,
                    () -> csvParser.parseToObject(csvFile, User.class));
        }

        @Test
        void testParseToObject_nestedCSVFile() {
            File csvFile = new File(TEST_RESOURCES + "nested.csv");

            List<Employee> result = csvParser.parseToObject(csvFile, Employee.class);

            assertAll("Nested Csv parsing",
                    () -> assertEquals(1, result.size()),
                    () -> assertTrue(result.get(0).isEmployed()),
                    () -> assertEquals(45, result.get(0).getAge()),
                    () -> assertEquals(200, result.get(0).getSalary()),
                    () -> assertEquals("john doe", result.get(0).getName()),
                    () -> assertEquals(Department.ENGINEERING, result.get(0).getDepartment()),
                    () -> assertEquals("10001", result.get(0).getAddress().getZipCode()),
                    () -> assertEquals("New York", result.get(0).getAddress().getCity()),
                    () -> assertEquals("2022-01-29", result.get(0).getStartDate().toString())
            );
        }
    }
}