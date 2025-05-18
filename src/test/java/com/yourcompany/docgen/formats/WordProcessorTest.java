package com.yourcompany.docgen.formats;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class WordProcessorTest {
    @Test
    void testProcessTemplate() throws Exception {
        // Create a minimal DOCX with a placeholder
        XWPFDocument doc = new XWPFDocument();
        XWPFParagraph para = doc.createParagraph();
        XWPFRun run = para.createRun();
        run.setText("Hello, ${name}!");
        File template = Files.createTempFile("test-docx-template", ".docx").toFile();
        try (FileOutputStream fos = new FileOutputStream(template)) { doc.write(fos); }

        // Prepare data
        Map<String, Object> data = new HashMap<>();
        data.put("name", "World");
        File output = Files.createTempFile("test-docx-output", ".docx").toFile();

        // Process template
        WordProcessor processor = new WordProcessor();
        processor.processTemplate(template.getAbsolutePath(), output.getAbsolutePath(), data);
        assertTrue(output.exists());
        assertTrue(output.length() > 0);
        // Optionally, read back and check text
        try (XWPFDocument outDoc = new XWPFDocument(Files.newInputStream(output.toPath()))) {
            String text = outDoc.getParagraphArray(0).getText();
            assertTrue(text.contains("World"));
        }
    }
} 