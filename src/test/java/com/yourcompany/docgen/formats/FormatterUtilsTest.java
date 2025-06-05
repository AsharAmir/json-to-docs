package com.yourcompany.docgen.formats;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class FormatterUtilsTest {
    @Test
    void testFormatDateISO() {
        assertEquals("2020-01-01", FormatterUtils.formatDateISO("2020-01-01"));
        long ts = 1577836800000L; // 2020-01-01 UTC
        assertEquals("2020-01-01", FormatterUtils.formatDateISO(ts));
        assertNull(FormatterUtils.formatDateISO(null));
        assertNull(FormatterUtils.formatDateISO("not-a-date"));
    }

    @Test
    void testGetWeekNumber() {
        assertEquals(1, FormatterUtils.getWeekNumber("2020-01-01"));
        long ts = 1577836800000L; // 2020-01-01 UTC
        assertEquals(1, FormatterUtils.getWeekNumber(ts));
        assertNull(FormatterUtils.getWeekNumber(null));
        assertNull(FormatterUtils.getWeekNumber("not-a-date"));
    }

    @Test
    void testTextCaseManipulation() {
        assertEquals("abc", FormatterUtils.toLower("ABC"));
        assertEquals("ABC", FormatterUtils.toUpper("abc"));
        assertEquals("Abc", FormatterUtils.capitalize("abc"));
        assertEquals("Abc Def", FormatterUtils.capitalizeWords("abc def"));
        assertNull(FormatterUtils.toLower(null));
        assertNull(FormatterUtils.toUpper(null));
        assertNull(FormatterUtils.capitalize(null));
        assertNull(FormatterUtils.capitalizeWords(null));
    }

    @Test
    void testPadding() {
        assertEquals("   abc", FormatterUtils.padLeft("abc", 6, " "));
        assertEquals("abc   ", FormatterUtils.padRight("abc", 6, " "));
        assertEquals("000abc", FormatterUtils.padLeft("abc", 6, "0"));
        assertEquals("abc000", FormatterUtils.padRight("abc", 6, "0"));
        assertEquals("abc", FormatterUtils.padLeft("abc", 3, "0"));
        assertEquals("abc", FormatterUtils.padRight("abc", 3, "0"));
        assertEquals("000", FormatterUtils.padLeft(null, 3, "0"));
        assertEquals("000", FormatterUtils.padRight(null, 3, "0"));
    }

    @Test
    void testNumberOperations() {
        assertEquals(5.0, FormatterUtils.add(2, 3));
        assertEquals(-1.0, FormatterUtils.subtract(2, 3));
        assertEquals(6.0, FormatterUtils.multiply(2, 3));
        assertEquals(2.0, FormatterUtils.divide(6, 3));
        assertNull(FormatterUtils.add(null, 3));
        assertNull(FormatterUtils.subtract(2, null));
        assertNull(FormatterUtils.multiply(null, null));
        assertNull(FormatterUtils.divide(2, 0));
    }

    @Test
    void testArrayJoinAndFlatten() {
        List<String> arr = Arrays.asList("a", "b", "c");
        assertEquals("a,b,c", FormatterUtils.join(arr, ","));
        assertEquals("a|b|c", FormatterUtils.join(arr, "|"));
        assertNull(FormatterUtils.join(null, ","));
        List<Object> nested = Arrays.asList("a", Arrays.asList("b", "c"), "d");
        assertEquals(Arrays.asList("a", "b", "c", "d"), FormatterUtils.flatten(nested));
        assertNull(FormatterUtils.flatten(null));
    }
} 