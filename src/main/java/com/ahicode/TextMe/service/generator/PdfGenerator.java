package com.ahicode.TextMe.service.generator;

import com.ahicode.TextMe.model.dto.report.ReportDto;

import java.io.IOException;

public interface PdfGenerator {
    byte[] generatePdf(ReportDto reportDto) throws IOException;
}
