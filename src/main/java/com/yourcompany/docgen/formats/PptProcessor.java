package com.yourcompany.docgen.formats;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class PptProcessor implements TemplateProcessor {
    @Override
    public void processTemplate(String templatePath, String outputPath, Map<String, Object> data) throws Exception {
        try (FileInputStream fis = new FileInputStream(templatePath);
             XMLSlideShow ppt = new XMLSlideShow(fis)) {
            for (XSLFSlide slide : ppt.getSlides()) {
                for (XSLFShape shape : slide.getShapes()) {
                    if (shape instanceof XSLFTextShape) {
                        XSLFTextShape textShape = (XSLFTextShape) shape;
                        String text = textShape.getText();
                        String replaced = replacePlaceholders(text, data);

                        // Remove all existing text paragraphs
                        List<XSLFTextParagraph> paragraphs = new ArrayList<>(textShape.getTextParagraphs());
                        for (XSLFTextParagraph para : paragraphs) {
                            textShape.removeTextParagraph(para);
                        }

                        // Add a new paragraph with the replaced text
                        textShape.addNewTextParagraph().addNewTextRun().setText(replaced);
                    }
                }
            }
            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                ppt.write(fos);
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