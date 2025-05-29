package com.yourcompany.docgen.formats;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class WordProcessorBuilderLikeTest {
    private static Map<String, WordProcessor.Formatter> getFormatters() {
        Map<String, WordProcessor.Formatter> formatters = new HashMap<>();
        formatters.put("int", (value, args) -> {
            try { return Integer.toString((int) Double.parseDouble(value)); } catch (Exception e) { return value; }
        });
        formatters.put("toFixed", (value, args) -> {
            int digits = args.length > 0 ? Integer.parseInt(args[0]) : 0;
            try { return String.format("%1$." + digits + "f", Double.parseDouble(value)); } catch (Exception e) { return value; }
        });
        formatters.put("custom", (value, args) -> "C-" + value);
        return formatters;
    }

    @Test
    void testFormatterInt() throws Exception {
        XWPFDocument doc = new XWPFDocument();
        XWPFParagraph para = doc.createParagraph();
        XWPFRun run = para.createRun();
        run.setText("Value: ${amount:int}");
        File template = Files.createTempFile("test-docx-int-template", ".docx").toFile();
        try (FileOutputStream fos = new FileOutputStream(template)) { doc.write(fos); }
        Map<String, Object> data = new HashMap<>();
        data.put("amount", 42.7);
        File output = Files.createTempFile("test-docx-int-output", ".docx").toFile();
        new WordProcessor().processTemplate(template.getAbsolutePath(), output.getAbsolutePath(), data, getFormatters());
        try (XWPFDocument outDoc = new XWPFDocument(Files.newInputStream(output.toPath()))) {
            String text = outDoc.getParagraphArray(0).getText();
            assertTrue(text.contains("Value: 42"));
        }
    }

    @Test
    void testFormatterToFixed() throws Exception {
        XWPFDocument doc = new XWPFDocument();
        XWPFParagraph para = doc.createParagraph();
        XWPFRun run = para.createRun();
        run.setText("Value: ${amount:toFixed(2)}");
        File template = Files.createTempFile("test-docx-tofixed-template", ".docx").toFile();
        try (FileOutputStream fos = new FileOutputStream(template)) { doc.write(fos); }
        Map<String, Object> data = new HashMap<>();
        data.put("amount", 42.789);
        File output = Files.createTempFile("test-docx-tofixed-output", ".docx").toFile();
        new WordProcessor().processTemplate(template.getAbsolutePath(), output.getAbsolutePath(), data, getFormatters());
        try (XWPFDocument outDoc = new XWPFDocument(Files.newInputStream(output.toPath()))) {
            String text = outDoc.getParagraphArray(0).getText();
            assertTrue(text.contains("Value: 42.79"));
        }
    }

    @Test
    void testFormatterCustom() throws Exception {
        XWPFDocument doc = new XWPFDocument();
        XWPFParagraph para = doc.createParagraph();
        XWPFRun run = para.createRun();
        run.setText("Value: ${amount:custom}");
        File template = Files.createTempFile("test-docx-custom-template", ".docx").toFile();
        try (FileOutputStream fos = new FileOutputStream(template)) { doc.write(fos); }
        Map<String, Object> data = new HashMap<>();
        data.put("amount", 99);
        File output = Files.createTempFile("test-docx-custom-output", ".docx").toFile();
        new WordProcessor().processTemplate(template.getAbsolutePath(), output.getAbsolutePath(), data, getFormatters());
        try (XWPFDocument outDoc = new XWPFDocument(Files.newInputStream(output.toPath()))) {
            String text = outDoc.getParagraphArray(0).getText();
            assertTrue(text.contains("Value: C-99"));
        }
    }

    @Test
    void testSafeValueAccessNullAndMissing() throws Exception {
        XWPFDocument doc = new XWPFDocument();
        XWPFParagraph para = doc.createParagraph();
        XWPFRun run = para.createRun();
        run.setText("Name: ${user.name}, Age: ${user.age}");
        File template = Files.createTempFile("test-docx-safe-template", ".docx").toFile();
        try (FileOutputStream fos = new FileOutputStream(template)) { doc.write(fos); }
        Map<String, Object> user = new HashMap<>();
        user.put("name", null);
        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        File output = Files.createTempFile("test-docx-safe-output", ".docx").toFile();
        new WordProcessor().processTemplate(template.getAbsolutePath(), output.getAbsolutePath(), data, getFormatters());
        try (XWPFDocument outDoc = new XWPFDocument(Files.newInputStream(output.toPath()))) {
            String text = outDoc.getParagraphArray(0).getText();
            assertTrue(text.contains("Name: , Age: "));
        }
    }

    @Test
    void testArrayIterationAndOrder() throws Exception {
        XWPFDocument doc = new XWPFDocument();
        XWPFTable table = doc.createTable(1, 2);
        table.getRow(0).getCell(0).setText("${items.name}");
        table.getRow(0).getCell(1).setText("${items.qty:int}");
        File template = Files.createTempFile("test-docx-array-template", ".docx").toFile();
        try (FileOutputStream fos = new FileOutputStream(template)) { doc.write(fos); }
        List<Map<String, Object>> items = new ArrayList<>();
        Map<String, Object> item1 = new HashMap<>(); item1.put("name", "Pen"); item1.put("qty", 2.5);
        Map<String, Object> item2 = new HashMap<>(); item2.put("name", "Pencil"); item2.put("qty", 5.9);
        items.add(item1); items.add(item2);
        Map<String, Object> data = new HashMap<>();
        data.put("items", items);
        File output = Files.createTempFile("test-docx-array-output", ".docx").toFile();
        new WordProcessor().processTemplate(template.getAbsolutePath(), output.getAbsolutePath(), data, getFormatters());
        try (XWPFDocument outDoc = new XWPFDocument(Files.newInputStream(output.toPath()))) {
            XWPFTable outTable = outDoc.getTableArray(0);
            assertEquals(2, outTable.getNumberOfRows());
            assertEquals("Pen", outTable.getRow(0).getCell(0).getText());
            assertEquals("2", outTable.getRow(0).getCell(1).getText());
            assertEquals("Pencil", outTable.getRow(1).getCell(0).getText());
            assertEquals("5", outTable.getRow(1).getCell(1).getText());
        }
    }
} 