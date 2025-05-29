package com.yourcompany.docgen.formats;

import org.apache.poi.xwpf.usermodel.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import java.util.function.Function;

public class WordProcessor implements TemplateProcessor {
    public interface Formatter {
        String format(String value, String... args);
    }

    @Override
    public void processTemplate(String templatePath, String outputPath, Map<String, Object> data, Map<String, Formatter> formatters) throws Exception {
        try (FileInputStream fis = new FileInputStream(templatePath);
             XWPFDocument doc = new XWPFDocument(fis)) {
            // Replace in paragraphs
            for (XWPFParagraph para : doc.getParagraphs()) {
                StringBuilder fullText = new StringBuilder();
                for (XWPFRun run : para.getRuns()) {
                    String text = run.getText(0);
                    if (text != null) {
                        fullText.append(text);
                    }
                }
                if (fullText.length() > 0) {
                    String replaced = replacePlaceholders(fullText.toString(), data, formatters);
                    int numRuns = para.getRuns().size();
                    for (int i = numRuns - 1; i >= 0; i--) {
                        para.removeRun(i);
                    }
                    XWPFRun newRun = para.createRun();
                    newRun.setText(replaced, 0);
                }
            }
            // Replace in table cells
            for (XWPFTable table : doc.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph para : cell.getParagraphs()) {
                            StringBuilder fullText = new StringBuilder();
                            for (XWPFRun run : para.getRuns()) {
                                String text = run.getText(0);
                                if (text != null) {
                                    fullText.append(text);
                                }
                            }
                            if (fullText.length() > 0) {
                                String replaced = replacePlaceholders(fullText.toString(), data, formatters);
                                int numRuns = para.getRuns().size();
                                for (int i = numRuns - 1; i >= 0; i--) {
                                    para.removeRun(i);
                                }
                                XWPFRun newRun = para.createRun();
                                newRun.setText(replaced, 0);
                            }
                        }
                    }
                }
            }
            // Table array iteration
            processTableArrays(doc, data, formatters);

            // Second pass: Replace in paragraphs again (for new rows)
            for (XWPFParagraph para : doc.getParagraphs()) {
                StringBuilder fullText = new StringBuilder();
                for (XWPFRun run : para.getRuns()) {
                    String text = run.getText(0);
                    if (text != null) {
                        fullText.append(text);
                    }
                }
                if (fullText.length() > 0) {
                    String replaced = replacePlaceholders(fullText.toString(), data, formatters);
                    int numRuns = para.getRuns().size();
                    for (int i = numRuns - 1; i >= 0; i--) {
                        para.removeRun(i);
                    }
                    XWPFRun newRun = para.createRun();
                    newRun.setText(replaced, 0);
                }
            }
            // Second pass: Replace in table cells again (for new rows)
            for (XWPFTable table : doc.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph para : cell.getParagraphs()) {
                            StringBuilder fullText = new StringBuilder();
                            for (XWPFRun run : para.getRuns()) {
                                String text = run.getText(0);
                                if (text != null) {
                                    fullText.append(text);
                                }
                            }
                            if (fullText.length() > 0) {
                                String replaced = replacePlaceholders(fullText.toString(), data, formatters);
                                int numRuns = para.getRuns().size();
                                for (int i = numRuns - 1; i >= 0; i--) {
                                    para.removeRun(i);
                                }
                                XWPFRun newRun = para.createRun();
                                newRun.setText(replaced, 0);
                            }
                        }
                    }
                }
            }
            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                doc.write(fos);
            }
        }
    }

    // Overload for backward compatibility
    @Override
    public void processTemplate(String templatePath, String outputPath, Map<String, Object> data) throws Exception {
        processTemplate(templatePath, outputPath, data, null);
    }

    private String replacePlaceholders(String text, Map<String, Object> data, Map<String, Formatter> formatters) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\$\\{([^}]+)\\}");
        java.util.regex.Matcher matcher = pattern.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String expr = matcher.group(1).trim();
            String[] parts = expr.split(":", 2);
            String keyExpr = parts[0].trim();
            Object value = resolveDeepKey(keyExpr, data);
            String replacement = value == null ? "" : value.toString();
            if (parts.length > 1 && formatters != null) {
                String formatterExpr = parts[1].trim();
                String formatterName;
                String[] formatterArgs = new String[0];
                if (formatterExpr.contains("(")) {
                    int idx = formatterExpr.indexOf('(');
                    formatterName = formatterExpr.substring(0, idx);
                    String argStr = formatterExpr.substring(idx + 1, formatterExpr.length() - 1);
                    formatterArgs = argStr.split(",");
                    for (int i = 0; i < formatterArgs.length; i++) {
                        formatterArgs[i] = formatterArgs[i].trim();
                    }
                } else {
                    formatterName = formatterExpr;
                }
                Formatter formatter = formatters.get(formatterName);
                if (formatter != null) {
                    replacement = formatter.format(replacement, formatterArgs);
                }
            }
            replacement = xmlEscape(replacement);
            matcher.appendReplacement(sb, java.util.regex.Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    // Overload for compatibility
    private String replacePlaceholders(String text, Map<String, Object> data) {
        return replacePlaceholders(text, data, null);
    }

    private void processTableArrays(XWPFDocument doc, Map<String, Object> data, Map<String, Formatter> formatters) {
        for (XWPFTable table : doc.getTables()) {
            List<XWPFTableRow> rows = new ArrayList<>(table.getRows());
            for (int rowIdx = 0; rowIdx < rows.size(); rowIdx++) {
                XWPFTableRow row = rows.get(rowIdx);
                StringBuilder rowTextBuilder = new StringBuilder();
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph para : cell.getParagraphs()) {
                        for (XWPFRun run : para.getRuns()) {
                            String text = run.getText(0);
                            if (text != null) {
                                rowTextBuilder.append(text);
                            }
                        }
                    }
                }
                String rowText = rowTextBuilder.toString();
                String arrayKey = getArrayKeyFromRow(rowText);
                if (arrayKey != null && data.get(arrayKey) instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> items = (List<Map<String, Object>>) data.get(arrayKey);
                    int insertIdx = rowIdx;
                    for (Map<String, Object> item : items) {
                        XWPFTableRow newRow = table.insertNewTableRow(insertIdx + 1);
                        cloneAndReplaceRow(row, newRow, arrayKey, item, formatters);
                        insertIdx++;
                    }
                    table.removeRow(rowIdx);
                    break;
                }
            }
        }
    }

    // Overload for compatibility
    private void processTableArrays(XWPFDocument doc, Map<String, Object> data) {
        processTableArrays(doc, data, null);
    }

    private void cloneAndReplaceRow(XWPFTableRow templateRow, XWPFTableRow newRow, String arrayKey, Map<String, Object> item, Map<String, Formatter> formatters) {
        while (newRow.getTableCells().size() > 0) {
            newRow.removeCell(0);
        }
        for (int i = 0; i < templateRow.getTableCells().size(); i++) {
            XWPFTableCell templateCell = templateRow.getCell(i);
            String cellXml = templateCell.getCTTc().xmlText();
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\$\\{([^}]+)\\}");
            java.util.regex.Matcher matcher = pattern.matcher(cellXml);
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                String expr = matcher.group(1).trim();
                String[] parts = expr.split(":", 2);
                String keyExpr = parts[0].trim();
                Object value = resolveDeepKey(keyExpr, item);
                String replacement = value == null ? "" : value.toString();
                if (parts.length > 1 && formatters != null) {
                    String formatterExpr = parts[1].trim();
                    String formatterName;
                    String[] formatterArgs = new String[0];
                    if (formatterExpr.contains("(")) {
                        int idx = formatterExpr.indexOf('(');
                        formatterName = formatterExpr.substring(0, idx);
                        String argStr = formatterExpr.substring(idx + 1, formatterExpr.length() - 1);
                        formatterArgs = argStr.split(",");
                        for (int j = 0; j < formatterArgs.length; j++) {
                            formatterArgs[j] = formatterArgs[j].trim();
                        }
                    } else {
                        formatterName = formatterExpr;
                    }
                    Formatter formatter = formatters.get(formatterName);
                    if (formatter != null) {
                        replacement = formatter.format(replacement, formatterArgs);
                    }
                }
                replacement = xmlEscape(replacement);
                matcher.appendReplacement(sb, java.util.regex.Matcher.quoteReplacement(replacement));
            }
            matcher.appendTail(sb);
            cellXml = sb.toString();
            XWPFTableCell newCell = newRow.addNewTableCell();
            try {
                org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc ctTc = org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc.Factory.parse(cellXml);
                newCell.getCTTc().set(ctTc);
            } catch (Exception e) {
                newCell.getCTTc().set(templateCell.getCTTc().copy());
            }
        }
    }

    private String getArrayKeyFromRow(String rowText) {
        // Looks for a placeholder like ${items.something} or ${items.nested.something} with optional spaces
        if (rowText == null) return null;
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("\\$\\{\\s*([a-zA-Z0-9_]+)\\s*(?:\\.[a-zA-Z0-9_]+)+\\s*\}").matcher(rowText);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String replaceArrayPlaceholders(String text, String arrayKey, Map<String, Object> item) {
        String result = text;
        for (Map.Entry<String, Object> entry : item.entrySet()) {
            String keyPattern = "\\$\\{" + arrayKey + "\\.\\s*" + entry.getKey() + "\\s*\\}";
            result = result.replaceAll(keyPattern, entry.getValue() == null ? "" : entry.getValue().toString());
        }
        return result;
    }

    private Object resolveDeepKey(String expr, Map<String, Object> data) {
        String[] parts = expr.split("\\.");
        Object current = data;
        for (String part : parts) {
            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(part);
            } else {
                return null;
            }
            if (current == null) return null;
        }
        return current;
    }

    private String xmlEscape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
} 