package com.yourcompany.docgen.formats;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class XmlUtilsTest {
    @Test
    void testFlattenXml() {
        List<String> parts = XmlUtils.flattenXml("<a>hello</a><b>world</b>");
        assertEquals(Arrays.asList("<a>", "hello", "</a>", "<b>", "world", "</b>"), parts);
        assertTrue(XmlUtils.flattenXml(null).isEmpty());
    }
    @Test
    void testRemoveMarkers() {
        assertEquals("<a>hello</a>", XmlUtils.removeMarkers("<a>${x}hello</a>"));
        assertNull(XmlUtils.removeMarkers(null));
    }
    @Test
    void testRemoveXml() {
        assertEquals("hello", XmlUtils.removeXml("<a>hello</a>"));
        assertNull(XmlUtils.removeXml(null));
    }
} 