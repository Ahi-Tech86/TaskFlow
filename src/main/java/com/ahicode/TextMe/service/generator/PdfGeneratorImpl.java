package com.ahicode.TextMe.service.generator;

import com.ahicode.TextMe.model.dto.report.ReportDto;
import com.ahicode.TextMe.service.HtmlConverter;
import com.ahicode.TextMe.service.ThymeleafService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfGeneratorImpl implements PdfGenerator {

    private final ThymeleafService thymeleafService;
    private final HtmlConverter htmlConverter;

    @Override
    public byte[] generatePdf(ReportDto report) throws IOException {
        String html = thymeleafService.generateHtml(report);
        return htmlConverter.convertHtmlToPdf(html);
    }
}
