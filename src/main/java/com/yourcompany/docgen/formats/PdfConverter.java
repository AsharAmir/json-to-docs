package com.yourcompany.docgen.formats;

import org.jodconverter.core.office.OfficeException;
import org.jodconverter.local.JodConverter;
import org.jodconverter.local.office.LocalOfficeManager;

import java.io.File;

public class PdfConverter {
    public void wordToPdf(String docxPath, String pdfPath) throws Exception {
        LocalOfficeManager officeManager = LocalOfficeManager.install();
        try {
            officeManager.start();
            JodConverter.convert(new File(docxPath)).to(new File(pdfPath)).execute();
        } finally {
            officeManager.stop();
        }
    }
}