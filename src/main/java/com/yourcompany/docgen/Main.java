package com.yourcompany.docgen;

public class Main {
    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println("Usage: java -jar document-generator.jar <input.json> <template> <outputDir> <format:docx|xlsx|pptx|odt> [pdf]");
            System.exit(1);
        }
        String jsonPath = args[0];
        String templatePath = args[1];
        String outputDir = args[2];
        String format = args[3].toLowerCase();
        boolean toPdf = args.length > 4 && args[4].equalsIgnoreCase("pdf");

        try {
            DocumentGenerator generator = new DocumentGenerator();
            switch (format) {
                case "docx":
                    generator.generateFromWordTemplate(jsonPath, templatePath, outputDir, toPdf);
                    break;
                case "xlsx":
                    generator.generateFromExcelTemplate(jsonPath, templatePath, outputDir, toPdf);
                    break;
                case "pptx":
                    generator.generateFromPptTemplate(jsonPath, templatePath, outputDir, toPdf);
                    break;
                case "odt":
                    generator.generateFromOdtTemplate(jsonPath, templatePath, outputDir, toPdf);
                    break;
                default:
                    System.out.println("Unsupported format: " + format);
                    System.exit(1);
            }
            System.out.println("Document generation completed.");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(2);
        }
    }
} 