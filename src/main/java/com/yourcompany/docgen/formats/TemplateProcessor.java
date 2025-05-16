package com.yourcompany.docgen.formats;

import java.util.Map;

public interface TemplateProcessor {
    void processTemplate(String templatePath, String outputPath, Map<String, Object> data) throws Exception;
} 