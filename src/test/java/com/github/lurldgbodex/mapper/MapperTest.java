package com.github.lurldgbodex.mapper;

import com.github.lurldgbodex.exceptions.ParserException;
import com.github.lurldgbodex.model.Address;
import com.github.lurldgbodex.model.Department;
import com.github.lurldgbodex.model.Employee;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MapperTest {
    private Mapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new Mapper();
    }

    @Nested
    class JsonXmlMappingTests {
        private static final String INVALID_JSON = "[{ invalid_json }]";
        private static final String VALID_JSON = """
                [{
                    "full_name": "John Doe",
                    "years": 30,
                    "active": true,
                    "salary": 75000.4,
                    "start_date": "2020-01-15",
                    "department": "ENGINEERING"
                }]""";

        @Test
        void mapJsonToObject_validFile_ReturnsCorrectObjects(@TempDir Path tempDir) throws IOException {
            File jsonFile = createTempFile(tempDir, "test.json", VALID_JSON);

            List<Employee> result = mapper.mapToObject(jsonFile, Employee.class, false);

            assertAll("Employee properties",
                    () -> assertEquals(1, result.size()),
                    () -> assertEquals("John Doe", result.get(0).getName()),
                    () -> assertEquals(30, result.get(0).getAge()),
                    () -> assertEquals(75000.4, result.get(0).getSalary()),
                    () -> assertEquals(LocalDate.of(2020, 1, 15), result.get(0).getStartDate()),
                    () -> assertEquals(Department.ENGINEERING, result.get(0).getDepartment())
            );
        }

        @Test
        void mapInvalidJson_throwsParserException(@TempDir Path tempDir) throws Exception {
            File jsonFile = createTempFile(tempDir, "invalid.json", INVALID_JSON);

            assertThrows(ParserException.class,
                    () -> mapper.mapToObject(jsonFile, Employee.class, false));
        }
    }

    @Nested
    class CsvMappingTests {
        @Test
        void mapCsvRecordToObject_ValidData_ReturnsCorrectObject() {
            CSVRecord mockRecord = mock(CSVRecord.class);
            when(mockRecord.get("full_name")).thenReturn("Alice Smith");
            when(mockRecord.get("years")).thenReturn("28");
            when(mockRecord.get("salary")).thenReturn("85000.75");
            when(mockRecord.get("active")).thenReturn("true");
            when(mockRecord.get("start_date")).thenReturn("2019-03-01");

            Employee result = mapper.mapToObject(
                    mockRecord, Employee.class, CSVRecord::get);

            assertAll("Employee properties",
                    () -> assertEquals("Alice Smith", result.getName()),
                    () -> assertEquals(28, result.getAge()),
                    () -> assertEquals(85000.75, result.getSalary()),
                    () -> assertTrue(result.isEmployed()),
                    () -> assertEquals(LocalDate.of(2019, 3, 1), result.getStartDate())
            );
        }

        @Test
        void mapCsvRecordWithMissingValues_skipsEmptyFields() {
            CSVRecord mockRecord = mock(CSVRecord.class);
            when(mockRecord.get("full_name")).thenReturn("Bob Johnson");
            when(mockRecord.get("years")).thenReturn("");
            when(mockRecord.get("salary")).thenReturn(null);

            Employee result = mapper.mapToObject(
                    mockRecord, Employee.class, CSVRecord::get);

            assertAll("Employee properties",
                    () -> assertEquals("Bob Johnson", result.getName()),
                    () -> assertEquals(0, result.getAge()),
                    () -> assertEquals(0.0, result.getSalary())
            );
        }
    }

    @Nested
    class EdgeCaseTests {
        @Test
        void mapInvalidNumericValue_ThrowsParserException() {
            CSVRecord mockRecord = mock(CSVRecord.class);
            when(mockRecord.get("years")).thenReturn("thirty");

            assertThrows(ParserException.class,
                    () -> mapper.mapToObject(mockRecord, Employee.class, CSVRecord::get));
        }

        @Test
        void mapCaseInsensitiveColumns_MatchesFieldsCorrectly() {
            CSVRecord mockRecord = mock(CSVRecord.class);
            when(mockRecord.get("FULL_NAME")).thenReturn("Carol Williams");

            Employee result = mapper.mapToObject(mockRecord, Employee.class,
                    (rec, col) -> rec.get(col.toUpperCase()));

            assertEquals("Carol Williams", result.getName());
        }

        @Test
        void mapInvalidEnumValue_ThrowsParserException() {
            CSVRecord mockRecord = mock(CSVRecord.class);
            when(mockRecord.get("department")).thenReturn("INVALID_DEPT");

            assertThrows(ParserException.class,
                    () -> mapper.mapToObject(mockRecord, Employee.class, CSVRecord::get));
        }

        @Test
        @Disabled
        void mapNestedObjects_CreatesNestedStructure() {
            CSVRecord mockRecord = mock(CSVRecord.class);

            when(mockRecord.get("address.city")).thenReturn("New York");
            when(mockRecord.get("address.zip")).thenReturn("10001");

            Employee employee = new Employee();
            employee.setAddress(new Address());

            when(mapper.mapToObject(mockRecord, Employee.class, CSVRecord::get)).thenReturn(employee);

            Employee result = mapper.mapToObject(
                    mockRecord, Employee.class, CSVRecord::get);

            assertAll("Address properties",
                    () -> assertEquals("New York", result.getAddress().getCity()),
                    () -> assertEquals("10001", result.getAddress().getZipCode())
            );
        }
    }

    private File createTempFile(Path tempDir, String filename, String content) throws IOException {
        Path filePath = tempDir.resolve(filename);
        Files.writeString(filePath, content);
        return filePath.toFile();
    }
}