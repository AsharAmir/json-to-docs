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

public class WordProcessorAdvancedTest {
    @Test
    void testNestedObjectPlaceholder() throws Exception {
        XWPFDocument doc = new XWPFDocument();
        XWPFParagraph para = doc.createParagraph();
        XWPFRun run = para.createRun();
        run.setText("Order: ${order.id}, Customer: ${order.customer.name}");
        File template = Files.createTempFile("test-docx-nested-template", ".docx").toFile();
        try (FileOutputStream fos = new FileOutputStream(template)) { doc.write(fos); }

        Map<String, Object> customer = new HashMap<>();
        customer.put("name", "Alice");
        Map<String, Object> order = new HashMap<>();
        order.put("id", 123);
        order.put("customer", customer);
        Map<String, Object> data = new HashMap<>();
        data.put("order", order);
        File output = Files.createTempFile("test-docx-nested-output", ".docx").toFile();

        WordProcessor processor = new WordProcessor();
        processor.processTemplate(template.getAbsolutePath(), output.getAbsolutePath(), data);
        try (XWPFDocument outDoc = new XWPFDocument(Files.newInputStream(output.toPath()))) {
            String text = outDoc.getParagraphArray(0).getText();
            assertTrue(text.contains("Order: 123, Customer: Alice"));
        }
    }

    @Test
    void testNullAndUndefinedHandling() throws Exception {
        XWPFDocument doc = new XWPFDocument();
        XWPFParagraph para = doc.createParagraph();
        XWPFRun run = para.createRun();
        run.setText("Name: ${user.name}, Age: ${user.age}");
        File template = Files.createTempFile("test-docx-null-template", ".docx").toFile();
        try (FileOutputStream fos = new FileOutputStream(template)) { doc.write(fos); }

        Map<String, Object> user = new HashMap<>();
        user.put("name", null);
        // age is missing
        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        File output = Files.createTempFile("test-docx-null-output", ".docx").toFile();

        WordProcessor processor = new WordProcessor();
        processor.processTemplate(template.getAbsolutePath(), output.getAbsolutePath(), data);
        try (XWPFDocument outDoc = new XWPFDocument(Files.newInputStream(output.toPath()))) {
            String text = outDoc.getParagraphArray(0).getText();
            assertTrue(text.contains("Name: , Age: "));
        }
    }

    @Test
    void testSpecialCharacterEscaping() throws Exception {
        XWPFDocument doc = new XWPFDocument();
        XWPFParagraph para = doc.createParagraph();
        XWPFRun run = para.createRun();
        run.setText("Special: ${special}");
        File template = Files.createTempFile("test-docx-special-escape-template", ".docx").toFile();
        try (FileOutputStream fos = new FileOutputStream(template)) { doc.write(fos); }

        Map<String, Object> data = new HashMap<>();
        data.put("special", "<>&\"'");
        File output = Files.createTempFile("test-docx-special-escape-output", ".docx").toFile();

        WordProcessor processor = new WordProcessor();
        processor.processTemplate(template.getAbsolutePath(), output.getAbsolutePath(), data);
        try (XWPFDocument outDoc = new XWPFDocument(Files.newInputStream(output.toPath()))) {
            String text = outDoc.getParagraphArray(0).getText();
            assertTrue(text.contains("Special: &lt;&gt;&amp;\"'"));
        }
    }

    @Test
    void testTableArrayIterationWithNestedObjects() throws Exception {
        XWPFDocument doc = new XWPFDocument();
        XWPFTable table = doc.createTable(1, 2);
        table.getRow(0).getCell(0).setText("${items.product.name}");
        table.getRow(0).getCell(1).setText("${items.qty}");
        File template = Files.createTempFile("test-docx-table-nested-template", ".docx").toFile();
        try (FileOutputStream fos = new FileOutputStream(template)) { doc.write(fos); }

        List<Map<String, Object>> items = new ArrayList<>();
        Map<String, Object> prod1 = new HashMap<>(); prod1.put("name", "Pen");
        Map<String, Object> item1 = new HashMap<>(); item1.put("product", prod1); item1.put("qty", 2);
        Map<String, Object> prod2 = new HashMap<>(); prod2.put("name", "Pencil");
        Map<String, Object> item2 = new HashMap<>(); item2.put("product", prod2); item2.put("qty", 5);
        items.add(item1); items.add(item2);
        Map<String, Object> data = new HashMap<>();
        data.put("items", items);
        File output = Files.createTempFile("test-docx-table-nested-output", ".docx").toFile();

        WordProcessor processor = new WordProcessor();
        processor.processTemplate(template.getAbsolutePath(), output.getAbsolutePath(), data);
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