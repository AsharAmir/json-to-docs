package com.yourcompany.docgen.formats;

import org.apache.poi.xwpf.usermodel.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;

public class WordProcessor implements TemplateProcessor {
    @Override
    public void processTemplate(String templatePath, String outputPath, Map<String, Object> data) throws Exception {
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
                    String replaced = replacePlaceholders(fullText.toString(), data);
                    // Remove all runs
                    int numRuns = para.getRuns().size();
                    for (int i = numRuns - 1; i >= 0; i--) {
                        para.removeRun(i);
                    }
                    // Add a single run with the replaced text
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
                                String replaced = replacePlaceholders(fullText.toString(), data);
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
            processTableArrays(doc, data);

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
                    String replaced = replacePlaceholders(fullText.toString(), data);
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
                                String replaced = replacePlaceholders(fullText.toString(), data);
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

    private void processTableArrays(XWPFDocument doc, Map<String, Object> data) {
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
                // Detect if this row is a template for an array (e.g., contains ${items.})
                String arrayKey = getArrayKeyFromRow(rowText);
                if (arrayKey != null && data.get(arrayKey) instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> items = (List<Map<String, Object>>) data.get(arrayKey);
                    int insertIdx = rowIdx;
                    for (Map<String, Object> item : items) {
                        XWPFTableRow newRow = table.insertNewTableRow(insertIdx + 1);
                        cloneAndReplaceRow(row, newRow, arrayKey, item);
                        insertIdx++;
                    }
                    table.removeRow(rowIdx); // Remove the template row
                    break; // Only process one array per table for simplicity
                }
            }
        }
    }

    private String getArrayKeyFromRow(String rowText) {
        // Looks for a placeholder like ${items.something} with optional spaces
        if (rowText == null) return null;
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("\\$\\{\\s*([a-zA-Z0-9_]+)\\s*\\.\\s*[a-zA-Z0-9_]+\\s*\\}").matcher(rowText);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private void cloneAndReplaceRow(XWPFTableRow templateRow, XWPFTableRow newRow, String arrayKey, Map<String, Object> item) {
        // Remove all cells from the new row
        while (newRow.getTableCells().size() > 0) {
            newRow.removeCell(0);
        }

        for (int i = 0; i < templateRow.getTableCells().size(); i++) {
            XWPFTableCell templateCell = templateRow.getCell(i);
            // Get the XML as a string
            String cellXml = templateCell.getCTTc().xmlText();

            // Replace placeholders in the XML string
            for (Map.Entry<String, Object> entry : item.entrySet()) {
                // Replace with and without spaces
                cellXml = cellXml.replaceAll("\\$\\{\\s*" + arrayKey + "\\." + entry.getKey() + "\\s*\\}", entry.getValue() == null ? "" : entry.getValue().toString());
            }

            // Create a new cell and set the modified XML
            XWPFTableCell newCell = newRow.addNewTableCell();
            try {
                // Parse the modified XML and set it to the new cell as CTTc
                CTTc ctTc = CTTc.Factory.parse(cellXml);
                newCell.getCTTc().set(ctTc);
            } catch (Exception e) {
                // Fallback: just copy the original cell if XML fails
                newCell.getCTTc().set(templateCell.getCTTc().copy());
            }
        }
    }

    private String replaceArrayPlaceholders(String text, String arrayKey, Map<String, Object> item) {
        String result = text;
        for (Map.Entry<String, Object> entry : item.entrySet()) {
            String keyPattern = "\\$\\{" + arrayKey + "\\.\\s*" + entry.getKey() + "\\s*\\}";
            result = result.replaceAll(keyPattern, entry.getValue() == null ? "" : entry.getValue().toString());
        }
        return result;
    }

    private String replacePlaceholders(String text, Map<String, Object> data) {
        String result = text;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String keyPattern = "\\$\\{\\s*" + entry.getKey() + "\\s*\\}";
            result = result.replaceAll(keyPattern, entry.getValue() == null ? "" : entry.getValue().toString());
        }
        return result;
    }
} 