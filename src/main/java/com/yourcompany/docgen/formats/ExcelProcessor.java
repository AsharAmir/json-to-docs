package com.yourcompany.docgen.formats;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;

public class ExcelProcessor implements TemplateProcessor {
    @Override
    public void processTemplate(String templatePath, String outputPath, Map<String, Object> data) throws Exception {
        try (FileInputStream fis = new FileInputStream(templatePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            for (Sheet sheet : workbook) {
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        if (cell.getCellType() == CellType.STRING) {
                            String text = cell.getStringCellValue();
                            String replaced = replacePlaceholders(text, data);
                            cell.setCellValue(replaced);
                        }
                    }
                }
            }
            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                workbook.write(fos);
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