package com.yourcompany.docgen.formats;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import static org.junit.jupiter.api.Assertions.*;

public class PdfConverterTest {
    @Test
    void testWordToPdfSuccess() throws Exception {
        // Create a minimal DOCX file
        XWPFDocument doc = new XWPFDocument();
        doc.createParagraph().createRun().setText("Hello PDF!");
        File docxFile = Files.createTempFile("test-docx-to-pdf", ".docx").toFile();
        try (FileOutputStream fos = new FileOutputStream(docxFile)) { doc.write(fos); }
        File pdfFile = Files.createTempFile("test-docx-to-pdf-output", ".pdf").toFile();
        PdfConverter converter = new PdfConverter();
        converter.wordToPdf(docxFile.getAbsolutePath(), pdfFile.getAbsolutePath());
        assertTrue(pdfFile.exists());
        assertTrue(pdfFile.length() > 0);
        // Optionally, check PDF is readable
        try (PDDocument pdfDoc = PDDocument.load(pdfFile)) {
            assertFalse(pdfDoc.getPages().isEmpty());
        }
    }

    @Test
    void testWordToPdfWithCorruptedInput() throws Exception {
        // Create a fake/corrupted DOCX file
        File docxFile = Files.createTempFile("test-corrupt-docx", ".docx").toFile();
        try (FileOutputStream fos = new FileOutputStream(docxFile)) { fos.write(new byte[]{0,1,2,3,4,5}); }
        File pdfFile = Files.createTempFile("test-corrupt-docx-output", ".pdf").toFile();
        PdfConverter converter = new PdfConverter();
        Exception ex = assertThrows(Exception.class, () -> {
            converter.wordToPdf(docxFile.getAbsolutePath(), pdfFile.getAbsolutePath());
        });
        assertNotNull(ex.getMessage());
    }

    @Test
    void testWordToPdfWithNonDocxInput() throws Exception {
        // Create a text file instead of DOCX
        File txtFile = Files.createTempFile("test-txt-to-pdf", ".txt").toFile();
        Files.writeString(txtFile.toPath(), "This is not a DOCX file.");
        File pdfFile = Files.createTempFile("test-txt-to-pdf-output", ".pdf").toFile();
        PdfConverter converter = new PdfConverter();
        Exception ex = assertThrows(Exception.class, () -> {
            converter.wordToPdf(txtFile.getAbsolutePath(), pdfFile.getAbsolutePath());
        });
        assertNotNull(ex.getMessage());
    }
} 