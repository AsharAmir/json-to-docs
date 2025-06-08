package com.yourcompany.docgen.formats;

import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.stream.Collectors;

public class FormatterUtils {
    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;

    public static String formatDateISO(String dateStr) {
        if (dateStr == null) {
            return null;
        }
        try {
            LocalDate date = LocalDate.parse(dateStr, ISO_DATE);
            return date.format(ISO_DATE);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static String formatDateISO(Long timestamp) {
        if (timestamp == null) {
            return null;
        }
        try {
            LocalDate date = Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
            return date.format(ISO_DATE);
        } catch (Exception e) {
            return null;
        }
    }

    public static Integer getWeekNumber(String dateStr) {
        if (dateStr == null) {
            return null;
        }
        try {
            LocalDate date = LocalDate.parse(dateStr, ISO_DATE);
            return date.get(WeekFields.ISO.weekOfWeekBasedYear());
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static Integer getWeekNumber(Long timestamp) {
        if (timestamp == null) {
            return null;
        }
        try {
            LocalDate date = Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
            return date.get(WeekFields.ISO.weekOfWeekBasedYear());
        } catch (Exception e) {
            return null;
        }
    }

    public static String toLower(String text) {
        return text != null ? text.toLowerCase() : null;
    }

    public static String toUpper(String text) {
        return text != null ? text.toUpperCase() : null;
    }

    public static String capitalize(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    public static String capitalizeWords(String text) {
        if (text == null) {
            return null;
        }
        return Arrays.stream(text.split("\\s+"))
            .map(FormatterUtils::capitalize)
            .collect(Collectors.joining(" "));
    }

    public static String padLeft(String text, int length, String padChar) {
        if (text == null) {
            return padChar.repeat(length);
        }
        if (text.length() >= length) {
            return text;
        }
        return padChar.repeat(length - text.length()) + text;
    }

    public static String padRight(String text, int length, String padChar) {
        if (text == null) {
            return padChar.repeat(length);
        }
        if (text.length() >= length) {
            return text;
        }
        return text + padChar.repeat(length - text.length());
    }

    public static Double add(Number a, Number b) {
        if (a == null || b == null) {
            return null;
        }
        return a.doubleValue() + b.doubleValue();
    }

    public static Double subtract(Number a, Number b) {
        if (a == null || b == null) {
            return null;
        }
        return a.doubleValue() - b.doubleValue();
    }

    public static Double multiply(Number a, Number b) {
        if (a == null || b == null) {
            return null;
        }
        return a.doubleValue() * b.doubleValue();
    }

    public static Double divide(Number a, Number b) {
        if (a == null || b == null || b.doubleValue() == 0) {
            return null;
        }
        return a.doubleValue() / b.doubleValue();
    }

    public static String join(List<?> list, String delimiter) {
        if (list == null) {
            return null;
        }
        return list.stream()
            .map(Object::toString)
            .collect(Collectors.joining(delimiter));
    }

    public static List<Object> flatten(List<?> list) {
        if (list == null) {
            return null;
        }
        List<Object> result = new ArrayList<>();
        for (Object item : list) {
            if (item instanceof List) {
                result.addAll(flatten((List<?>) item));
            } else {
                result.add(item);
            }
        }
        return result;
    }
} 