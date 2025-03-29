package com.github.lurldgbodex.parsers;

import com.github.lurldgbodex.exceptions.ParserException;
import com.github.lurldgbodex.model.Department;
import com.github.lurldgbodex.model.Employee;
import com.github.lurldgbodex.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class XmlParserTest {
    private static final String TEST_RESOURCES = "src/test/resources/xml/";
    private XmlParser parser;

    @BeforeEach
    void setup() {
        parser = new XmlParser();

    }

    @Nested
    class TestParseObject {

        @Test
        void testParserObject_validXml() {
            File xmlFile = new File(TEST_RESOURCES + "valid.xml");

            List<User> result = parser.parseToObject(xmlFile, User.class);

            assertAll("User Properties",
                    () -> assertEquals(1, result.size()),
                    () -> assertEquals("John Doe", result.get(0).getName()),
                    () -> assertEquals("john@doe.com", result.get(0).getEmail()),
                    () -> assertTrue(result.get(0).isActive())
            );
        }

        @Test
        void testParseObject_invalidXml() {
            File xmlFile = new File(TEST_RESOURCES + "invalid.xml");

            assertThrows(ParserException.class,
                    () -> parser.parseToObject(xmlFile, User.class));
        }

        @Test
        void testParseObject_nestedXml() {
            File xmlFile = new File(TEST_RESOURCES + "nested.xml");

            List<Employee> result = parser.parseToObject(xmlFile, Employee.class);

            assertAll("Nested Csv parsing",
                    () -> assertEquals(2, result.size()),
                    () -> assertTrue(result.get(0).isEmployed()),
                    () -> assertEquals(30, result.get(0).getAge()),
                    () -> assertEquals(89500, result.get(0).getSalary()),
                    () -> assertEquals("John Doe", result.get(0).getName()),
                    () -> assertEquals(Department.ENGINEERING, result.get(0).getDepartment()),
                    () -> assertEquals("10001", result.get(0).getAddress().getZipCode()),
                    () -> assertEquals("New York", result.get(0).getAddress().getCity()),
                    () -> assertEquals("2022-05-15", result.get(0).getStartDate().toString())
            );
        }

        @Test
        @Disabled("Unimplemented")
        void testParseObject_validation_failure() {}

        @Test
        @Disabled("Unimplemented")
        void testParseObject_incompatibleClass_invalidated() {}

        @Test
        @Disabled("Unimplemented")
        void testParseObject_incompatibleClass_validated() {}
    }
}
