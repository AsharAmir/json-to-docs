package com.yourcompany.docgen.formats;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class MarkerExtractorTest {
    @Test
    void testEmptyDescriptorIfNoMarkers() {
        MarkerExtractor.Descriptor desc = MarkerExtractor.extractDescriptor("Hello world!");
        assertNotNull(desc);
        assertTrue(desc.markers.isEmpty());
        assertTrue(desc.attributes.isEmpty());
    }

    @Test
    void testSingleMarkerDescriptor() {
        MarkerExtractor.Descriptor desc = MarkerExtractor.extractDescriptor("Hello, ${name}!");
        assertEquals(1, desc.markers.size());
        assertTrue(desc.markers.contains("name"));
        assertTrue(desc.attributes.containsKey("name"));
        assertEquals(List.of("name"), desc.attributes.get("name"));
    }

    @Test
    void testMultipleAttributes() {
        MarkerExtractor.Descriptor desc = MarkerExtractor.extractDescriptor("${user.name, user.age}");
        assertEquals(1, desc.markers.size());
        assertTrue(desc.markers.get(0).contains("user.name, user.age"));
        assertEquals(List.of("user.name", "user.age"), desc.attributes.get("user.name, user.age"));
    }

    @Test
    void testTwoLevelsOfObject() {
        MarkerExtractor.Descriptor desc = MarkerExtractor.extractDescriptor("${user.name}");
        assertEquals(1, desc.markers.size());
        assertTrue(desc.markers.contains("user.name"));
        assertEquals(List.of("user.name"), desc.attributes.get("user.name"));
    }

    @Test
    void testCleanUnwantedSpaces() {
        MarkerExtractor.Descriptor desc = MarkerExtractor.extractDescriptor("${  user.name   ,   user.age   }");
        assertEquals(1, desc.markers.size());
        assertTrue(desc.markers.get(0).contains("user.name , user.age"));
        assertEquals(List.of("user.name", "user.age"), desc.attributes.get("user.name , user.age"));
    }
} 