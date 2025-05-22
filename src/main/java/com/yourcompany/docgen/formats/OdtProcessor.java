package com.yourcompany.docgen.formats;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;

public class OdtProcessor implements TemplateProcessor {
    @Override
    public void processTemplate(String templatePath, String outputPath, Map<String, Object> data) throws Exception {
        // Just copy the file for now to avoid corruption
        Files.copy(new File(templatePath).toPath(), new File(outputPath).toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
} 