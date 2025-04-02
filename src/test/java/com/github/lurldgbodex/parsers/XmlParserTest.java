package com.github.lurldgbodex.parsers;

import com.github.lurldgbodex.exceptions.ParserException;
import com.github.lurldgbodex.model.Department;
import com.github.lurldgbodex.model.Employee;
import com.github.lurldgbodex.model.Person;
import com.github.lurldgbodex.model.User;
import org.junit.jupiter.api.BeforeEach;
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
        void testParseObject_validation_failure() {
            File xmlFile = new File(TEST_RESOURCES + "invalid-name.xml");
            File XmlFile2 = new File(TEST_RESOURCES + "invalid-email.xml");
            File xmlFile3 = new File(TEST_RESOURCES + "missing-name.xml");

            assertThrows(ParserException.class,
                    () -> parser.parseToObject(xmlFile, Person.class));
            assertThrows(ParserException.class,
                    () -> parser.parseToObject(XmlFile2, Person.class));
            assertThrows(ParserException.class,
                    () -> parser.parseToObject(xmlFile3, Person.class));
        }

        @Test
        void testParseObject_incompatibleClass_invalidated() {
            File xmlFile = new File(TEST_RESOURCES + "missing-name.xml");
            List<User> result = parser.parseToObject(xmlFile, User.class);

            assertAll("Default User props",
                    () -> assertNull(result.get(0).getName()),
                    () -> assertNull(result.get(0).getEmail()),
                    () -> assertFalse(result.get(0).isActive())
            );
        }

        @Test
        void testParseObject_incompatibleClass_validated() {
            File xmlFile = new File(TEST_RESOURCES + "nested.xml");

            assertThrows(ParserException.class,
                    () -> parser.parseToObject(xmlFile, Person.class));
        }

        @Test
        void testParsedObject_validation_passed() {
            File file = new File(TEST_RESOURCES + "validated.xml");

            List<Person> result = parser.parseToObject(file, Person.class);

            assertAll("Person Properties",
                    () -> assertEquals(1, result.size()),
                    () -> assertEquals(1, result.get(0).getId()),
                    () -> assertEquals(0, result.get(0).getAge()),
                    () -> assertEquals("Michael Jordan", result.get(0).getFullName())
            );
        }
    }
}
