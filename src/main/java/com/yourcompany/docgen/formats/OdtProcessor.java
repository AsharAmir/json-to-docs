package com.yourcompany.docgen.formats;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class OdtProcessor implements TemplateProcessor {
    @Override
    public void processTemplate(String templatePath, String outputPath, Map<String, Object> data) throws Exception {
        // TODO: Implement real ODT placeholder replacement using ODFDOM or similar
        // For now, just copy the file as a placeholder
        copyFile(templatePath, outputPath);
    }

    private void copyFile(String src, String dest) throws IOException {
        try (FileInputStream fis = new FileInputStream(src);
             FileOutputStream fos = new FileOutputStream(dest)) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
        }
    }
} 