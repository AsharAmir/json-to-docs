package com.yourcompany.docgen.formats;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

public class WordProcessor implements TemplateProcessor {
    @Override
    public void processTemplate(String templatePath, String outputPath, Map<String, Object> data) throws Exception {
        try (FileInputStream fis = new FileInputStream(templatePath);
             XWPFDocument doc = new XWPFDocument(fis)) {
            for (XWPFParagraph para : doc.getParagraphs()) {
                for (XWPFRun run : para.getRuns()) {
                    String text = run.getText(0);
                    if (text != null) {
                        String replaced = replacePlaceholders(text, data);
                        run.setText(replaced, 0);
                    }
                }
            }
            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                doc.write(fos);
            }
        }
    }

    private String replacePlaceholders(String text, Map<String, Object> data) {
        String result = text;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = "${" + entry.getKey() + "}";
            result = result.replace(key, entry.getValue() == null ? "" : entry.getValue().toString());
        }
        return result;
    }
} 