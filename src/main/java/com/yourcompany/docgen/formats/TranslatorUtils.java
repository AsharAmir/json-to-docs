package com.yourcompany.docgen.formats;

import java.util.*;
import java.util.regex.*;

public class TranslatorUtils {
    private static final Map<String, Map<String, String>> langFiles = new HashMap<>();

    public static List<String> findTranslationMarkers(String s) {
        List<String> keys = new ArrayList<>();
        if (s == null) return keys;
        Matcher m = Pattern.compile("t\\(([^)]+)\\)").matcher(s);
        while (m.find()) keys.add(m.group(1));
        return keys;
    }
    public static Map<String, String> loadLangFile(String lang) {
        return langFiles.getOrDefault(lang, new HashMap<>());
    }
    public static void saveLangFile(String lang, Map<String, String> data) {
        langFiles.put(lang, new HashMap<>(data));
    }
} 