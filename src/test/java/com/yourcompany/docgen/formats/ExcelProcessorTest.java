package com.yourcompany.docgen.formats;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class ExcelProcessorTest {
    @Test
    void testProcessTemplate() throws Exception {
        // Create a minimal XLSX with a placeholder
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet();
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue("${item}");
        File template = Files.createTempFile("test-xlsx-template", ".xlsx").toFile();
        try (FileOutputStream fos = new FileOutputStream(template)) { wb.write(fos); }

        // Prepare data
        Map<String, Object> data = new HashMap<>();
        data.put("item", "Apple");
        File output = Files.createTempFile("test-xlsx-output", ".xlsx").toFile();

        // Process template
        ExcelProcessor processor = new ExcelProcessor();
        processor.processTemplate(template.getAbsolutePath(), output.getAbsolutePath(), data);
        assertTrue(output.exists());
        assertTrue(output.length() > 0);
        // Optionally, read back and check cell value
        try (Workbook outWb = new XSSFWorkbook(Files.newInputStream(output.toPath()))) {
            Sheet outSheet = outWb.getSheetAt(0);
            Row outRow = outSheet.getRow(0);
            Cell outCell = outRow.getCell(0);
            assertEquals("Apple", outCell.getStringCellValue());
        }
    }
} 