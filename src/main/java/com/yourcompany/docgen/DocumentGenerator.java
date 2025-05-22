package com.yourcompany.docgen;

import com.yourcompany.docgen.formats.*;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.local.JodConverter;
import org.jodconverter.local.office.LocalOfficeManager;
import java.io.File;
import java.util.List;
import java.util.Map;

public class DocumentGenerator {
    public void generateFromWordTemplate(String jsonPath, String templatePath, String outputDir, boolean toPdf) throws Exception {
        DataParser parser = new DataParser();
        Map<String, Object> data = parser.parseJsonObject(jsonPath);
        WordProcessor wordProcessor = new WordProcessor();
        PdfConverter pdfConverter = new PdfConverter();
        String outDocx = outputDir + "/output_1.docx";
        wordProcessor.processTemplate(templatePath, outDocx, data);
        if (toPdf) {
            String outPdf = outputDir + "/output_1.pdf";
            pdfConverter.wordToPdf(outDocx, outPdf);
        }
    }

    public void generateFromExcelTemplate(String jsonPath, String templatePath, String outputDir, boolean toPdf) throws Exception {
        DataParser parser = new DataParser();
        List<Map<String, Object>> records = parser.parseJsonArray(jsonPath);
        ExcelProcessor excelProcessor = new ExcelProcessor();
        for (int i = 0; i < records.size(); i++) {
            Map<String, Object> data = records.get(i);
            String outXlsx = outputDir + "/output_" + (i+1) + ".xlsx";
            excelProcessor.processTemplate(templatePath, outXlsx, data);
            if (toPdf) {
                String outPdf = outputDir + "/output_" + (i+1) + ".pdf";
                convertToPdfWithJodConverter(outXlsx, outPdf);
            }
        }
    }

    public void generateFromPptTemplate(String jsonPath, String templatePath, String outputDir, boolean toPdf) throws Exception {
        DataParser parser = new DataParser();
        List<Map<String, Object>> records = parser.parseJsonArray(jsonPath);
        PptProcessor pptProcessor = new PptProcessor();
        for (int i = 0; i < records.size(); i++) {
            Map<String, Object> data = records.get(i);
            String outPptx = outputDir + "/output_" + (i+1) + ".pptx";
            pptProcessor.processTemplate(templatePath, outPptx, data);
            if (toPdf) {
                String outPdf = outputDir + "/output_" + (i+1) + ".pdf";
                convertToPdfWithJodConverter(outPptx, outPdf);
            }
        }
    }

    public void generateFromOdtTemplate(String jsonPath, String templatePath, String outputDir, boolean toPdf) throws Exception {
        DataParser parser = new DataParser();
        List<Map<String, Object>> records = parser.parseJsonArray(jsonPath);
        OdtProcessor odtProcessor = new OdtProcessor();
        for (int i = 0; i < records.size(); i++) {
            Map<String, Object> data = records.get(i);
            String outOdt = outputDir + "/output_" + (i+1) + ".odt";
            odtProcessor.processTemplate(templatePath, outOdt, data);
            if (toPdf) {
                String outPdf = outputDir + "/output_" + (i+1) + ".pdf";
                convertToPdfWithJodConverter(outOdt, outPdf);
            }
        }
    }

    private void convertToPdfWithJodConverter(String inputPath, String outputPath) throws OfficeException {
        LocalOfficeManager officeManager = LocalOfficeManager.install();
        try {
            officeManager.start();
            JodConverter.convert(new File(inputPath)).to(new File(outputPath)).execute();
        } finally {
            officeManager.stop();
        }
    }
} 