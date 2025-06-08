package com.yourcompany.docgen.formats;

import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.text.Paragraph;
import java.io.*;
import java.util.Map;

public class OdtProcessor implements DocumentProcessor {
    @Override
    public void processTemplate(String templatePath, String outputPath, Map<String, Object> data) throws Exception {
        if (templatePath == null || outputPath == null || data == null) {
            throw new IllegalArgumentException("Template path, output path, and data cannot be null");
        }

        File templateFile = new File(templatePath);
        if (!templateFile.exists()) {
            throw new FileNotFoundException("Template file not found: " + templatePath);
        }

        try (TextDocument doc = TextDocument.loadDocument(templateFile)) {
            // Process each paragraph in the document
            for (Paragraph para : doc.getParagraphIterator()) {
                String text = para.getTextContent();
                if (text != null && text.contains("${")) {
                    // Replace placeholders with data
                    String newText = replacePlaceholders(text, data);
                    para.setTextContent(newText);
                }
            }

            // Save the processed document
            doc.save(new File(outputPath));
        } catch (Exception e) {
            throw new Exception("Failed to process ODT template: " + e.getMessage(), e);
        }
    }

    private String replacePlaceholders(String text, Map<String, Object> data) {
        StringBuilder result = new StringBuilder(text);
        int startIndex;
        while ((startIndex = result.indexOf("${")) != -1) {
            int endIndex = result.indexOf("}", startIndex);
            if (endIndex == -1) break;

            String placeholder = result.substring(startIndex + 2, endIndex);
            Object value = data.get(placeholder);
            String replacement = value != null ? value.toString() : "";

            result.replace(startIndex, endIndex + 1, replacement);
        }
        return result.toString();
    }
} 