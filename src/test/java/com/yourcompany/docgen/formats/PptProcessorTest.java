package com.yourcompany.docgen.formats;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class PptProcessorTest {
    @Test
    void testProcessTemplate() throws Exception {
        // Create a minimal PPTX with a placeholder
        XMLSlideShow ppt = new XMLSlideShow();
        XSLFSlide slide = ppt.createSlide();
        XSLFTextShape shape = slide.createTextBox();
        shape.setText("Hello, ${who}!");
        File template = Files.createTempFile("test-pptx-template", ".pptx").toFile();
        try (FileOutputStream fos = new FileOutputStream(template)) { ppt.write(fos); }

        // Prepare data
        Map<String, Object> data = new HashMap<>();
        data.put("who", "World");
        File output = Files.createTempFile("test-pptx-output", ".pptx").toFile();

        // Process template
        PptProcessor processor = new PptProcessor();
        processor.processTemplate(template.getAbsolutePath(), output.getAbsolutePath(), data);
        assertTrue(output.exists());
        assertTrue(output.length() > 0);
        // Optionally, read back and check text
        try (XMLSlideShow outPpt = new XMLSlideShow(Files.newInputStream(output.toPath()))) {
            XSLFSlide outSlide = outPpt.getSlides().get(0);
            XSLFTextShape outShape = (XSLFTextShape) outSlide.getShapes().get(0);
            assertTrue(outShape.getText().contains("World"));
        }
    }
} 