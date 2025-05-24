package com.ahicode.TextMe.service;

import com.ahicode.TextMe.model.dto.report.ReportDto;

public interface ThymeleafService {
    String generateHtml(ReportDto reportDto);
}
