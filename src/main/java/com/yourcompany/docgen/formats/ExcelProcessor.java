package com.yourcompany.docgen.formats;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class ExcelProcessor implements DocumentProcessor {
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");

    @Override
    public void processTemplate(String templatePath, String outputPath, Map<String, Object> data) throws Exception {
        if (templatePath == null || outputPath == null || data == null) {
            throw new IllegalArgumentException("Template path, output path, and data cannot be null");
        }

        File templateFile = new File(templatePath);
        if (!templateFile.exists()) {
            throw new FileNotFoundException("Template file not found: " + templatePath);
        }

        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(templateFile))) {
            // Process each sheet
            for (Sheet sheet : workbook) {
                processSheet(sheet, data);
            }

            // Save the processed workbook
            try (FileOutputStream out = new FileOutputStream(outputPath)) {
                workbook.write(out);
            }
        } catch (Exception e) {
            throw new Exception("Failed to process Excel template: " + e.getMessage(), e);
        }
    }

    private void processSheet(Sheet sheet, Map<String, Object> data) {
        for (Row row : sheet) {
            for (Cell cell : row) {
                processCell(cell, data);
            }
        }
    }

    private void processCell(Cell cell, Map<String, Object> data) {
        if (cell == null) return;

        String cellValue = getCellValueAsString(cell);
        if (cellValue != null && cellValue.contains("${")) {
            String newValue = replacePlaceholders(cellValue, data);
            setCellValue(cell, newValue);
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    private void setCellValue(Cell cell, String value) {
        if (cell == null) return;

        // Try to parse as number if possible
        try {
            double numValue = Double.parseDouble(value);
            cell.setCellValue(numValue);
        } catch (NumberFormatException e) {
            // If not a number, set as string
            cell.setCellValue(value);
        }
    }

    private String replacePlaceholders(String text, Map<String, Object> data) {
        StringBuilder result = new StringBuilder(text);
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        
        while (matcher.find()) {
            String placeholder = matcher.group(1).trim();
            Object value = getNestedValue(data, placeholder);
            String replacement = value != null ? value.toString() : "";
            result.replace(matcher.start(), matcher.end(), replacement);
        }
        
        return result.toString();
    }

    private Object getNestedValue(Map<String, Object> data, String key) {
        String[] parts = key.split("\\.");
        Object current = data;
        
        for (String part : parts) {
            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(part);
            } else {
                return null;
            }
        }
        
        return current;
    }
} 