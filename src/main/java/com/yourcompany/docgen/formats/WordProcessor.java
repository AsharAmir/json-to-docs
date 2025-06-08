package com.yourcompany.docgen.formats;

import org.apache.poi.xwpf.usermodel.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class WordProcessor implements DocumentProcessor {
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");
    private Map<String, Formatter> formatters;

    public interface Formatter {
        String format(String value, String... args);
    }

    public WordProcessor() {
        this.formatters = new HashMap<>();
        // Add default formatters
        formatters.put("int", (value, args) -> {
            try {
                return Integer.toString((int) Double.parseDouble(value));
            } catch (Exception e) {
                return value;
            }
        });
        formatters.put("toFixed", (value, args) -> {
            int digits = args.length > 0 ? Integer.parseInt(args[0]) : 0;
            try {
                return String.format("%1$." + digits + "f", Double.parseDouble(value));
            } catch (Exception e) {
                return value;
            }
        });
    }

    @Override
    public void processTemplate(String templatePath, String outputPath, Map<String, Object> data) throws Exception {
        processTemplate(templatePath, outputPath, data, formatters);
    }

    public void processTemplate(String templatePath, String outputPath, Map<String, Object> data, 
                              Map<String, Formatter> customFormatters) throws Exception {
        if (templatePath == null || outputPath == null || data == null) {
            throw new IllegalArgumentException("Template path, output path, and data cannot be null");
        }

        File templateFile = new File(templatePath);
        if (!templateFile.exists()) {
            throw new FileNotFoundException("Template file not found: " + templatePath);
        }

        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(templateFile))) {
            // Process paragraphs
            for (XWPFParagraph para : doc.getParagraphs()) {
                processParagraph(para, data, customFormatters);
            }

            // Process tables
            for (XWPFTable table : doc.getTables()) {
                processTable(table, data, customFormatters);
            }

            // Save the processed document
            try (FileOutputStream out = new FileOutputStream(outputPath)) {
                doc.write(out);
            }
        } catch (Exception e) {
            throw new Exception("Failed to process Word template: " + e.getMessage(), e);
        }
    }

    private void processParagraph(XWPFParagraph para, Map<String, Object> data, 
                                Map<String, Formatter> formatters) {
        String text = para.getText();
        if (text.contains("${")) {
            List<XWPFRun> runs = para.getRuns();
            if (runs != null && !runs.isEmpty()) {
                // Clear existing runs
                for (int i = runs.size() - 1; i >= 0; i--) {
                    para.removeRun(i);
                }
                // Add new run with processed text
                XWPFRun run = para.createRun();
                run.setText(replacePlaceholders(text, data, formatters));
                // Copy formatting from first run if available
                if (!runs.isEmpty()) {
                    XWPFRun firstRun = runs.get(0);
                    run.setBold(firstRun.isBold());
                    run.setItalic(firstRun.isItalic());
                    run.setUnderline(firstRun.getUnderline());
                    run.setFontSize(firstRun.getFontSize());
                    run.setFontFamily(firstRun.getFontFamily());
                }
            }
        }
    }

    private void processTable(XWPFTable table, Map<String, Object> data, 
                            Map<String, Formatter> formatters) {
        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                for (XWPFParagraph para : cell.getParagraphs()) {
                    processParagraph(para, data, formatters);
                }
            }
        }
    }

    private String replacePlaceholders(String text, Map<String, Object> data, 
                                     Map<String, Formatter> formatters) {
        StringBuilder result = new StringBuilder(text);
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        
        while (matcher.find()) {
            String placeholder = matcher.group(1);
            String[] parts = placeholder.split(":");
            String key = parts[0].trim();
            String formatterName = parts.length > 1 ? parts[1].trim() : null;
            
            Object value = getNestedValue(data, key);
            String replacement = value != null ? value.toString() : "";
            
            if (formatterName != null && formatters.containsKey(formatterName)) {
                String[] args = new String[0];
                if (formatterName.contains("(")) {
                    int start = formatterName.indexOf("(");
                    int end = formatterName.indexOf(")");
                    if (end > start) {
                        args = formatterName.substring(start + 1, end).split(",");
                        formatterName = formatterName.substring(0, start);
                    }
                }
                replacement = formatters.get(formatterName).format(replacement, args);
            }
            
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