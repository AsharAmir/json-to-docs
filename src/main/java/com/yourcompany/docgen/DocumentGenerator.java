package com.yourcompany.docgen;

import com.yourcompany.docgen.formats.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class DocumentGenerator {
    private final DataParser dataParser;
    private final WordProcessor wordProcessor;
    private final ExcelProcessor excelProcessor;
    private final PptProcessor pptProcessor;
    private final OdtProcessor odtProcessor;
    private final PdfConverter pdfConverter;

    public DocumentGenerator() {
        this.dataParser = new DataParser();
        this.wordProcessor = new WordProcessor();
        this.excelProcessor = new ExcelProcessor();
        this.pptProcessor = new PptProcessor();
        this.odtProcessor = new OdtProcessor();
        this.pdfConverter = new PdfConverter();
    }

    public void generateFromWordTemplate(String jsonPath, String templatePath, String outputDir, boolean generatePdf) throws Exception {
        List<Map<String, Object>> data = dataParser.parseJsonArray(jsonPath);
        generateDocuments(data, templatePath, outputDir, "docx", wordProcessor, generatePdf);
    }

    public void generateFromExcelTemplate(String jsonPath, String templatePath, String outputDir, boolean generatePdf) throws Exception {
        List<Map<String, Object>> data = dataParser.parseJsonArray(jsonPath);
        generateDocuments(data, templatePath, outputDir, "xlsx", excelProcessor, generatePdf);
    }

    public void generateFromPptTemplate(String jsonPath, String templatePath, String outputDir, boolean generatePdf) throws Exception {
        List<Map<String, Object>> data = dataParser.parseJsonArray(jsonPath);
        generateDocuments(data, templatePath, outputDir, "pptx", pptProcessor, generatePdf);
    }

    public void generateFromOdtTemplate(String jsonPath, String templatePath, String outputDir, boolean generatePdf) throws Exception {
        List<Map<String, Object>> data = dataParser.parseJsonArray(jsonPath);
        generateDocuments(data, templatePath, outputDir, "odt", odtProcessor, generatePdf);
    }

    private void generateDocuments(List<Map<String, Object>> data, String templatePath, String outputDir, 
                                 String extension, DocumentProcessor processor, boolean generatePdf) throws Exception {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("No data to process");
        }

        Path outputPath = Paths.get(outputDir);
        if (!Files.exists(outputPath)) {
            Files.createDirectories(outputPath);
        }

        for (int i = 0; i < data.size(); i++) {
            String outputFile = outputPath.resolve("output_" + (i + 1) + "." + extension).toString();
            processor.processTemplate(templatePath, outputFile, data.get(i));

            if (generatePdf) {
                String pdfFile = outputPath.resolve("output_" + (i + 1) + ".pdf").toString();
                pdfConverter.wordToPdf(outputFile, pdfFile);
            }
        }
    }
} 