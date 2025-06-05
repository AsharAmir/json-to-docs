package com.yourcompany.docgen.formats;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class MathParserTest {
    @Test
    void testEvalSimpleExpressions() {
        assertEquals(5.0, MathParser.eval("2+3"));
        assertEquals(6.0, MathParser.eval("2*3"));
        assertEquals(2.0, MathParser.eval("6/3"));
        assertEquals(1.0, MathParser.eval("4-3"));
        assertEquals(7.0, MathParser.eval("1+2*3"));
        assertEquals(9.0, MathParser.eval("(1+2)*3"));
    }
    @Test
    void testEvalInvalidExpressions() {
        assertNull(MathParser.eval("2+abc"));
        assertNull(MathParser.eval(null));
        assertNull(MathParser.eval(""));
    }
    @Test
    void testExtractMarkers() {
        List<String> markers = MathParser.extractMarkers("Hello ${a+b} and ${c}");
        assertEquals(Arrays.asList("a+b", "c"), markers);
        assertTrue(MathParser.extractMarkers(null).isEmpty());
    }
} 