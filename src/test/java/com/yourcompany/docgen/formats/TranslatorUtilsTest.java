package com.yourcompany.docgen.formats;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class TranslatorUtilsTest {
    @Test
    void testFindTranslationMarkers() {
        List<String> keys = TranslatorUtils.findTranslationMarkers("Hello t(key1) and t(key2)");
        assertEquals(Arrays.asList("key1", "key2"), keys);
        assertTrue(TranslatorUtils.findTranslationMarkers(null).isEmpty());
    }
    @Test
    void testLoadSaveLangFile() {
        Map<String, String> data = new HashMap<>();
        data.put("hello", "Bonjour");
        TranslatorUtils.saveLangFile("fr", data);
        Map<String, String> loaded = TranslatorUtils.loadLangFile("fr");
        assertEquals("Bonjour", loaded.get("hello"));
        assertTrue(TranslatorUtils.loadLangFile("de").isEmpty());
    }
} 