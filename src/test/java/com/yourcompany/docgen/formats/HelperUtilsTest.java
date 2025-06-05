package com.yourcompany.docgen.formats;

import org.junit.jupiter.api.Test;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class HelperUtilsTest {
    @Test
    void testUniqueIdAndRandomId() {
        String id1 = HelperUtils.uniqueId();
        String id2 = HelperUtils.uniqueId();
        assertNotNull(id1);
        assertNotNull(id2);
        assertNotEquals(id1, id2);
        String rand = HelperUtils.randomId(10);
        assertNotNull(rand);
        assertEquals(10, rand.length());
    }

    @Test
    void testSafeFilename() {
        assertEquals("abc_def.txt", HelperUtils.safeFilename("abc*def.txt"));
        assertNull(HelperUtils.safeFilename(null));
    }

    @Test
    void testReadRemoveCopyDirectory() throws Exception {
        Path tempDir = Files.createTempDirectory("testdir");
        Path file1 = Files.writeString(tempDir.resolve("a.sql"), "SELECT 1;");
        Path file2 = Files.writeString(tempDir.resolve("b.txt"), "Hello");
        Map<String, String> sqlFiles = HelperUtils.readDirectory(tempDir.toString(), ".sql");
        assertTrue(sqlFiles.containsKey("a.sql"));
        assertFalse(sqlFiles.containsKey("b.txt"));
        Path copyDir = Files.createTempDirectory("copydir");
        HelperUtils.copyDirectory(tempDir.toString(), copyDir.toString());
        assertTrue(Files.exists(copyDir.resolve("a.sql")));
        assertTrue(Files.exists(copyDir.resolve("b.txt")));
        assertTrue(HelperUtils.removeDirectory(tempDir.toString()));
        assertFalse(Files.exists(tempDir));
    }

    @Test
    void testCleanJsVarAndRemoveQuotes() {
        assertEquals("abc_123", HelperUtils.cleanJsVar("abc-123"));
        assertEquals("abc", HelperUtils.removeQuotes("\"abc\""));
        assertEquals("abc", HelperUtils.removeQuotes("'abc'"));
        assertEquals("abc", HelperUtils.removeQuotes("abc"));
        assertNull(HelperUtils.cleanJsVar(null));
        assertNull(HelperUtils.removeQuotes(null));
    }

    @Test
    void testStringDistance() {
        assertEquals(0, HelperUtils.stringDistance("", ""));
        assertEquals(2, HelperUtils.stringDistance("ab", "cd"));
        assertEquals(1, HelperUtils.stringDistance("abc", "ab"));
        assertEquals(-1, HelperUtils.stringDistance(null, "a"));
    }

    @Test
    void testGetPathValue() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> sub = new HashMap<>();
        sub.put("b", 42);
        map.put("a", sub);
        assertEquals(42, HelperUtils.getPathValue(map, "a.b"));
        assertNull(HelperUtils.getPathValue(map, "a.c"));
        assertNull(HelperUtils.getPathValue(null, "a.b"));
    }

    @Test
    void testDeduplicate() {
        List<Integer> arr = Arrays.asList(1, 2, 2, 3, 1);
        List<Integer> dedup = HelperUtils.deduplicate(arr);
        assertEquals(Arrays.asList(1, 2, 3), dedup);
        assertNull(HelperUtils.deduplicate(null));
    }
} 