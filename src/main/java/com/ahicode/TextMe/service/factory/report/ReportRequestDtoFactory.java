package com.ahicode.TextMe.service.factory.report;

import com.ahicode.TextMe.model.dto.report.ReportRequestDto;
import org.springframework.stereotype.Component;

@Component
public class ReportRequestDtoFactory {

    public ReportRequestDto makeReportRequestDto(Long projectId, String email) {
        return ReportRequestDto.builder()
                .projectId(projectId)
                .email(email)
                .build();
    }
}
