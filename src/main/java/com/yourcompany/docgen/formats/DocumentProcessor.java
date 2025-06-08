package com.yourcompany.docgen.formats;

import java.util.Map;

public interface DocumentProcessor {
    void processTemplate(String templatePath, String outputPath, Map<String, Object> data) throws Exception;
} 