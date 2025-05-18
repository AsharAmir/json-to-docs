package com.yourcompany.docgen;

import org.junit.jupiter.api.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DocumentGeneratorTest {
    private static final String JSON_PATH = "src/main/resources/sample.json";
    private static final String DOCX_TEMPLATE = "src/main/resources/templates/sample_template.docx";
    private static final String XLSX_TEMPLATE = "src/main/resources/templates/sample_template.xlsx";
    private static final String PPTX_TEMPLATE = "src/main/resources/templates/sample_template.pptx";
    private static final String ODT_TEMPLATE = "src/main/resources/templates/sample_template.odt";
    private static Path outputDir;
    private static DocumentGenerator generator;

    @BeforeAll
    static void setup() throws Exception {
        outputDir = Files.createTempDirectory("docgen-test-" + UUID.randomUUID());
        generator = new DocumentGenerator();
    }

    @Test
    @Order(1)
    void testGenerateDocx() throws Exception {
        generator.generateFromWordTemplate(JSON_PATH, DOCX_TEMPLATE, outputDir.toString(), false);
        assertOutputFiles("docx");
    }

    @Test
    @Order(2)
    void testGenerateDocxPdf() throws Exception {
        generator.generateFromWordTemplate(JSON_PATH, DOCX_TEMPLATE, outputDir.toString(), true);
        assertOutputFiles("docx");
        assertOutputFiles("pdf");
    }

    @Test
    @Order(3)
    void testGenerateXlsx() throws Exception {
        generator.generateFromExcelTemplate(JSON_PATH, XLSX_TEMPLATE, outputDir.toString(), false);
        assertOutputFiles("xlsx");
    }

    @Test
    @Order(4)
    void testGenerateXlsxPdf() throws Exception {
        generator.generateFromExcelTemplate(JSON_PATH, XLSX_TEMPLATE, outputDir.toString(), true);
        assertOutputFiles("xlsx");
        assertOutputFiles("pdf");
    }

    @Test
    @Order(5)
    void testGeneratePptx() throws Exception {
        generator.generateFromPptTemplate(JSON_PATH, PPTX_TEMPLATE, outputDir.toString(), false);
        assertOutputFiles("pptx");
    }

    @Test
    @Order(6)
    void testGeneratePptxPdf() throws Exception {
        generator.generateFromPptTemplate(JSON_PATH, PPTX_TEMPLATE, outputDir.toString(), true);
        assertOutputFiles("pptx");
        assertOutputFiles("pdf");
    }

    @Test
    @Order(7)
    void testGenerateOdt() throws Exception {
        generator.generateFromOdtTemplate(JSON_PATH, ODT_TEMPLATE, outputDir.toString(), false);
        assertOutputFiles("odt");
    }

    @Test
    @Order(8)
    void testGenerateOdtPdf() throws Exception {
        generator.generateFromOdtTemplate(JSON_PATH, ODT_TEMPLATE, outputDir.toString(), true);
        assertOutputFiles("odt");
        assertOutputFiles("pdf");
    }

    private void assertOutputFiles(String ext) {
        for (int i = 1; i <= 2; i++) {
            File f = outputDir.resolve("output_" + i + "." + ext).toFile();
            Assertions.assertTrue(f.exists(), "File should exist: " + f.getAbsolutePath());
            Assertions.assertTrue(f.length() > 0, "File should not be empty: " + f.getAbsolutePath());
        }
    }
} 