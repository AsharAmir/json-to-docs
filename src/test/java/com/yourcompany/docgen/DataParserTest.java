package com.yourcompany.docgen;

import org.junit.jupiter.api.Test;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class DataParserTest {
    @Test
    void testParseValidJsonArray() throws Exception {
        String json = "[{\"a\":1},{\"b\":2}]";
        var file = Files.createTempFile("test-parse-json", ".json").toFile();
        try (FileWriter fw = new FileWriter(file)) { fw.write(json); }
        DataParser parser = new DataParser();
        List<Map<String, Object>> result = parser.parseJsonArray(file.getAbsolutePath());
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).get("a"));
        assertEquals(2, result.get(1).get("b"));
    }

    @Test
    void testParseEmptyArray() throws Exception {
        String json = "[]";
        var file = Files.createTempFile("test-parse-json-empty", ".json").toFile();
        try (FileWriter fw = new FileWriter(file)) { fw.write(json); }
        DataParser parser = new DataParser();
        List<Map<String, Object>> result = parser.parseJsonArray(file.getAbsolutePath());
        assertEquals(0, result.size());
    }

    @Test
    void testParseInvalidJson() throws Exception {
        String json = "not a json";
        var file = Files.createTempFile("test-parse-json-invalid", ".json").toFile();
        try (FileWriter fw = new FileWriter(file)) { fw.write(json); }
        DataParser parser = new DataParser();
        assertThrows(Exception.class, () -> parser.parseJsonArray(file.getAbsolutePath()));
    }
} 