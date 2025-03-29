package com.github.lurldgbodex.parsers;

import com.github.lurldgbodex.core.Parser;
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

public class JsonParserTest {
    private static final String TEST_RESOURCES = "src/test/resources/json/";
    private Parser jsonParser;

    @BeforeEach
    void setUp() {
        jsonParser = new JsonParser();
    }

    @Nested
    class TestParseToObject {

        @Test
        void testParseToObject_validJson() {
            File jsonFile = new File(TEST_RESOURCES + "valid.json");

            List<User> result = jsonParser.parseToObject(jsonFile, User.class);

            assertAll("User Properties",
                    () -> assertEquals(1, result.size()),
                    () -> assertEquals(2, result.get(0).getId()),
                    () -> assertEquals("John Doe", result.get(0).getName()),
                    () -> assertEquals("john@doe.com", result.get(0).getEmail()),
                    () -> assertFalse(result.get(0).isActive())
            );
        }

        @Test
        void testParseToObject_invalidJson() {
            File jsonFile = new File(TEST_RESOURCES + "invalid.json");
            assertThrows(ParserException.class,
                    () -> jsonParser.parseToObject(jsonFile, User.class));
        }

        @Test
        void testParseToObject_nestedJson() {
            File jsonFile = new File(TEST_RESOURCES + "nested.json");

            List<Employee> result = jsonParser.parseToObject(jsonFile, Employee.class);

            assertAll("Nested Csv parsing",
                    () -> assertEquals(2, result.size()),
                    () -> assertTrue(result.get(0).isEmployed()),
                    () -> assertEquals(20, result.get(0).getAge()),
                    () -> assertEquals(1020.5, result.get(0).getSalary()),
                    () -> assertEquals("john doe", result.get(0).getName()),
                    () -> assertEquals(Department.HR, result.get(0).getDepartment()),
                    () -> assertEquals("102022", result.get(0).getAddress().getZipCode()),
                    () -> assertEquals("Lagos", result.get(0).getAddress().getCity()),
                    () -> assertEquals("2014-11-12", result.get(0).getStartDate().toString())
            );
        }

        @Test
        @Disabled("unimplemented")
        void testParseObject_validation_failure() {}

        @Test
        @Disabled("Unimplemented")
        void testParseObject_incompatibleClass_invalidated() {}

        @Test
        @Disabled("Unimplemented")
        void testParseObject_incompatibleClass_validated() {}
    }
}
