package com.ahicode.TextMe.service.processor;

import com.ahicode.TextMe.model.dto.report.ReportRequestDto;

public interface DeserializeMessage {
    ReportRequestDto deserializeReportRequestDto(String message);
}
