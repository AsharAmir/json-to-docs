package com.yourcompany.docgen.formats;

import java.util.*;
import java.util.regex.*;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class MathParser {
    private static final Pattern MARKER_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");
    private static final ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");

    public static Double eval(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            return null;
        }

        try {
            // Sanitize the expression to only allow basic math operations
            if (!expression.matches("^[0-9+\\-*/().\\s]+$")) {
                return null;
            }
            
            Object result = engine.eval(expression);
            if (result instanceof Number) {
                return ((Number) result).doubleValue();
            }
            return null;
        } catch (ScriptException e) {
            return null;
        }
    }

    public static List<String> extractMarkers(String text) {
        if (text == null) {
            return new ArrayList<>();
        }

        List<String> markers = new ArrayList<>();
        Matcher matcher = MARKER_PATTERN.matcher(text);
        
        while (matcher.find()) {
            String content = matcher.group(1).trim();
            if (!content.isEmpty()) {
                markers.add(content);
            }
        }
        
        return markers;
    }
} 