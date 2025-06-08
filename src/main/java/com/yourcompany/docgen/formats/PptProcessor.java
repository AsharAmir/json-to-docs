package com.yourcompany.docgen.formats;

import org.apache.poi.xslf.usermodel.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class PptProcessor implements DocumentProcessor {
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

        try (XMLSlideShow ppt = new XMLSlideShow(new FileInputStream(templateFile))) {
            // Process each slide
            for (XSLFSlide slide : ppt.getSlides()) {
                processSlide(slide, data);
            }

            // Save the processed presentation
            try (FileOutputStream out = new FileOutputStream(outputPath)) {
                ppt.write(out);
            }
        } catch (Exception e) {
            throw new Exception("Failed to process PowerPoint template: " + e.getMessage(), e);
        }
    }

    private void processSlide(XSLFSlide slide, Map<String, Object> data) {
        for (XSLFShape shape : slide.getShapes()) {
            if (shape instanceof XSLFTextShape) {
                processTextShape((XSLFTextShape) shape, data);
            }
        }
    }

    private void processTextShape(XSLFTextShape shape, Map<String, Object> data) {
        String text = shape.getText();
        if (text != null && text.contains("${")) {
            String newText = replacePlaceholders(text, data);
            shape.setText(newText);
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