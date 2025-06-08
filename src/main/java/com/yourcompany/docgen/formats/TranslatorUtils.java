package com.yourcompany.docgen.formats;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class TranslatorUtils {
    private static final Pattern TRANSLATION_PATTERN = Pattern.compile("t\\(([^)]+)\\)");
    private static final String LANG_DIR = "lang";

    public static List<String> findTranslationMarkers(String text) {
        if (text == null) {
            return new ArrayList<>();
        }

        List<String> keys = new ArrayList<>();
        Matcher matcher = TRANSLATION_PATTERN.matcher(text);
        
        while (matcher.find()) {
            String key = matcher.group(1).trim();
            if (!key.isEmpty()) {
                keys.add(key);
            }
        }
        
        return keys;
    }

    public static void saveLangFile(String lang, Map<String, String> data) throws IOException {
        if (lang == null || data == null) {
            return;
        }

        Path langDir = Paths.get(LANG_DIR);
        if (!Files.exists(langDir)) {
            Files.createDirectories(langDir);
        }

        Path langFile = langDir.resolve(lang + ".properties");
        Properties props = new Properties();
        props.putAll(data);
        
        try (OutputStream out = Files.newOutputStream(langFile)) {
            props.store(out, "Language file for " + lang);
        }
    }

    public static Map<String, String> loadLangFile(String lang) {
        if (lang == null) {
            return new HashMap<>();
        }

        Path langFile = Paths.get(LANG_DIR, lang + ".properties");
        if (!Files.exists(langFile)) {
            return new HashMap<>();
        }

        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(langFile)) {
            props.load(in);
            Map<String, String> translations = new HashMap<>();
            for (String key : props.stringPropertyNames()) {
                translations.put(key, props.getProperty(key));
            }
            return translations;
        } catch (IOException e) {
            return new HashMap<>();
        }
    }
} 