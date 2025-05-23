package com.ahicode.TextMe.service.generator;

import com.ahicode.TextMe.model.dto.report.ReportDto;

import java.io.ByteArrayOutputStream;

public interface PdfGenerator {
    ByteArrayOutputStream generatePdf(ReportDto reportDto);
}
