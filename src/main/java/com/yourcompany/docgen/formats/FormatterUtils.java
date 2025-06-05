package com.yourcompany.docgen.formats;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FormatterUtils {
    // Date formatting
    public static String formatDateISO(Object value) {
        if (value == null) return null;
        try {
            if (value instanceof Long || value instanceof Integer) {
                Instant instant = Instant.ofEpochMilli(((Number) value).longValue());
                return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate().toString();
            }
            if (value instanceof String) {
                LocalDate date = LocalDate.parse((String) value);
                return date.toString();
            }
        } catch (Exception e) { return null; }
        return null;
    }

    public static Integer getWeekNumber(Object value) {
        if (value == null) return null;
        try {
            LocalDate date;
            if (value instanceof Long || value instanceof Integer) {
                Instant instant = Instant.ofEpochMilli(((Number) value).longValue());
                date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
            } else {
                date = LocalDate.parse(value.toString());
            }
            return date.get(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        } catch (Exception e) { return null; }
    }

    // Text case manipulation
    public static String toLower(String s) {
        return s == null ? null : s.toLowerCase();
    }
    public static String toUpper(String s) {
        return s == null ? null : s.toUpperCase();
    }
    public static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
    public static String capitalizeWords(String s) {
        if (s == null || s.isEmpty()) return s;
        String[] words = s.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) sb.append(capitalize(word));
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    // Padding
    public static String padLeft(String s, int len, String pad) {
        if (s == null) s = "";
        if (pad == null || pad.isEmpty()) pad = " ";
        while (s.length() < len) s = pad + s;
        return s;
    }
    public static String padRight(String s, int len, String pad) {
        if (s == null) s = "";
        if (pad == null || pad.isEmpty()) pad = " ";
        while (s.length() < len) s = s + pad;
        return s;
    }

    // Number operations
    public static Double add(Number a, Number b) {
        if (a == null || b == null) return null;
        return a.doubleValue() + b.doubleValue();
    }
    public static Double subtract(Number a, Number b) {
        if (a == null || b == null) return null;
        return a.doubleValue() - b.doubleValue();
    }
    public static Double multiply(Number a, Number b) {
        if (a == null || b == null) return null;
        return a.doubleValue() * b.doubleValue();
    }
    public static Double divide(Number a, Number b) {
        if (a == null || b == null || b.doubleValue() == 0) return null;
        return a.doubleValue() / b.doubleValue();
    }

    // Array join/map
    public static String join(List<?> arr, String sep) {
        if (arr == null) return null;
        if (sep == null) sep = ",";
        return String.join(sep, arr.stream().map(Object::toString).toArray(String[]::new));
    }
    public static List<Object> flatten(List<?> arr) {
        if (arr == null) return null;
        List<Object> flat = new ArrayList<>();
        for (Object o : arr) {
            if (o instanceof List) flat.addAll(flatten((List<?>) o));
            else flat.add(o);
        }
        return flat;
    }
} 