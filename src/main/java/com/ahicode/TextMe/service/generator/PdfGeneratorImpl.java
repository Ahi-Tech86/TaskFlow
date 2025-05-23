package com.ahicode.TextMe.service.generator;

import com.ahicode.TextMe.exception.AppException;
import com.ahicode.TextMe.model.dto.report.ReportDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Slf4j
@Service
public class PdfGeneratorImpl implements PdfGenerator {

    @Override
    public ByteArrayOutputStream generatePdf(ReportDto report) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (PDDocument pdfDocument = new PDDocument()) {
            PDPage page = new PDPage();
            pdfDocument.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(pdfDocument, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("Hello world");
                contentStream.endText();
            }

            pdfDocument.save(outputStream);

        } catch (Exception e) {
            log.error("An error occurred {} while writing the PDF document", e.toString());
            throw new AppException("An error occurred while writing the PDF document", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return outputStream;
    }
}
