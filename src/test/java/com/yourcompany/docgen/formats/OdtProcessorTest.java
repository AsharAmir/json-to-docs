package com.yourcompany.docgen.formats;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class OdtProcessorTest {
    @Test
    void testProcessTemplate() throws Exception {
        // Create a minimal ODT (just a text file for this test)
        File template = Files.createTempFile("test-odt-template", ".odt").toFile();
        try (FileWriter fw = new FileWriter(template)) { fw.write("Hello, ${name}!"); }

        // Prepare data
        Map<String, Object> data = new HashMap<>();
        data.put("name", "World");
        File output = Files.createTempFile("test-odt-output", ".odt").toFile();

        // Process template
        OdtProcessor processor = new OdtProcessor();
        processor.processTemplate(template.getAbsolutePath(), output.getAbsolutePath(), data);
        assertTrue(output.exists());
        assertTrue(output.length() > 0);
        // Optionally, check content matches (since OdtProcessor just copies)
        String templateContent = Files.readString(template.toPath());
        String outputContent = Files.readString(output.toPath());
        assertEquals(templateContent, outputContent);
    }
} 