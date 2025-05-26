package com.yourcompany.docgen.formats;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
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

    @Test
    void testTableCellPlaceholderReplacement() throws Exception {
        XWPFDocument doc = new XWPFDocument();
        XWPFTable table = doc.createTable(1, 1);
        table.getRow(0).getCell(0).setText("Item: ${item}");
        File template = Files.createTempFile("test-docx-table-template", ".docx").toFile();
        try (FileOutputStream fos = new FileOutputStream(template)) { doc.write(fos); }

        Map<String, Object> data = new HashMap<>();
        data.put("item", "Apple");
        File output = Files.createTempFile("test-docx-table-output", ".docx").toFile();

        WordProcessor processor = new WordProcessor();
        processor.processTemplate(template.getAbsolutePath(), output.getAbsolutePath(), data);
        try (XWPFDocument outDoc = new XWPFDocument(Files.newInputStream(output.toPath()))) {
            XWPFTable outTable = outDoc.getTableArray(0);
            String cellText = outTable.getRow(0).getCell(0).getText();
            assertTrue(cellText.contains("Apple"));
        }
    }

    @Test
    void testTableArrayIteration() throws Exception {
        XWPFDocument doc = new XWPFDocument();
        XWPFTable table = doc.createTable(1, 2);
        table.getRow(0).getCell(0).setText("${items.name}");
        table.getRow(0).getCell(1).setText("${items.qty}");
        File template = Files.createTempFile("test-docx-table-array-template", ".docx").toFile();
        try (FileOutputStream fos = new FileOutputStream(template)) { doc.write(fos); }

        java.util.List<Map<String, Object>> items = new java.util.ArrayList<>();
        Map<String, Object> item1 = new HashMap<>(); item1.put("name", "Pen"); item1.put("qty", 2);
        Map<String, Object> item2 = new HashMap<>(); item2.put("name", "Pencil"); item2.put("qty", 5);
        items.add(item1); items.add(item2);
        Map<String, Object> data = new HashMap<>();
        data.put("items", items);
        File output = Files.createTempFile("test-docx-table-array-output", ".docx").toFile();

        WordProcessor processor = new WordProcessor();
        processor.processTemplate(template.getAbsolutePath(), output.getAbsolutePath(), data);
        try (XWPFDocument outDoc = new XWPFDocument(Files.newInputStream(output.toPath()))) {
            XWPFTable outTable = outDoc.getTableArray(0);
            assertEquals(2, outTable.getNumberOfRows()); // Template row removed, 2 rows for 2 items
            assertEquals("Pen", outTable.getRow(0).getCell(0).getText());
            assertEquals("2", outTable.getRow(0).getCell(1).getText());
            assertEquals("Pencil", outTable.getRow(1).getCell(0).getText());
            assertEquals("5", outTable.getRow(1).getCell(1).getText());
        }
    }

    @Test
    void testTableArrayIterationEmptyArray() throws Exception {
        XWPFDocument doc = new XWPFDocument();
        XWPFTable table = doc.createTable(1, 2);
        table.getRow(0).getCell(0).setText("${items.name}");
        table.getRow(0).getCell(1).setText("${items.qty}");
        File template = Files.createTempFile("test-docx-table-array-empty-template", ".docx").toFile();
        try (FileOutputStream fos = new FileOutputStream(template)) { doc.write(fos); }

        Map<String, Object> data = new HashMap<>();
        data.put("items", new java.util.ArrayList<>());
        File output = Files.createTempFile("test-docx-table-array-empty-output", ".docx").toFile();

        WordProcessor processor = new WordProcessor();
        processor.processTemplate(template.getAbsolutePath(), output.getAbsolutePath(), data);
        try (XWPFDocument outDoc = new XWPFDocument(Files.newInputStream(output.toPath()))) {
            XWPFTable outTable = outDoc.getTableArray(0);
            assertEquals(0, outTable.getNumberOfRows()); // Template row removed, no rows added
        }
    }

    @Test
    void testMissingKeyPlaceholder() throws Exception {
        XWPFDocument doc = new XWPFDocument();
        XWPFParagraph para = doc.createParagraph();
        XWPFRun run = para.createRun();
        run.setText("Hello, ${missing}!");
        File template = Files.createTempFile("test-docx-missing-template", ".docx").toFile();
        try (FileOutputStream fos = new FileOutputStream(template)) { doc.write(fos); }

        Map<String, Object> data = new HashMap<>();
        File output = Files.createTempFile("test-docx-missing-output", ".docx").toFile();

        WordProcessor processor = new WordProcessor();
        processor.processTemplate(template.getAbsolutePath(), output.getAbsolutePath(), data);
        try (XWPFDocument outDoc = new XWPFDocument(Files.newInputStream(output.toPath()))) {
            String text = outDoc.getParagraphArray(0).getText();
            assertTrue(text.contains("${missing}")); // Placeholder remains
        }
    }

    @Test
    void testSpecialCharacters() throws Exception {
        XWPFDocument doc = new XWPFDocument();
        XWPFParagraph para = doc.createParagraph();
        XWPFRun run = para.createRun();
        run.setText("Special: ${special}");
        File template = Files.createTempFile("test-docx-special-template", ".docx").toFile();
        try (FileOutputStream fos = new FileOutputStream(template)) { doc.write(fos); }

        Map<String, Object> data = new HashMap<>();
        data.put("special", "<>&\"'");
        File output = Files.createTempFile("test-docx-special-output", ".docx").toFile();

        WordProcessor processor = new WordProcessor();
        processor.processTemplate(template.getAbsolutePath(), output.getAbsolutePath(), data);
        try (XWPFDocument outDoc = new XWPFDocument(Files.newInputStream(output.toPath()))) {
            String text = outDoc.getParagraphArray(0).getText();
            assertTrue(text.contains("<>&\"'"));
        }
    }

    @Test
    void testPlaceholderSplitAcrossRuns() throws Exception {
        XWPFDocument doc = new XWPFDocument();
        XWPFParagraph para = doc.createParagraph();
        XWPFRun run1 = para.createRun();
        run1.setText("Hello, ${na");
        XWPFRun run2 = para.createRun();
        run2.setText("me}!");
        File template = Files.createTempFile("test-docx-split-template", ".docx").toFile();
        try (FileOutputStream fos = new FileOutputStream(template)) { doc.write(fos); }

        Map<String, Object> data = new HashMap<>();
        data.put("name", "World");
        File output = Files.createTempFile("test-docx-split-output", ".docx").toFile();

        WordProcessor processor = new WordProcessor();
        processor.processTemplate(template.getAbsolutePath(), output.getAbsolutePath(), data);
        try (XWPFDocument outDoc = new XWPFDocument(Files.newInputStream(output.toPath()))) {
            String text = outDoc.getParagraphArray(0).getText();
            assertTrue(text.contains("World"));
        }
    }
} 