package com.ahicode.TextMe.service.impl;

import com.ahicode.TextMe.model.dto.report.ReportDto;
import com.ahicode.TextMe.service.ThymeleafService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class ThymeleafServiceImpl implements ThymeleafService {

    private final TemplateEngine templateEngine;

    @Override
    public String generateHtml(ReportDto report) {
        Context context = new Context();
        context.setVariable("report", report);
        return templateEngine.process("report_template", context);
    }
}
