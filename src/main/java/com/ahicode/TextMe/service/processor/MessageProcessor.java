package com.ahicode.TextMe.service.processor;

import com.ahicode.TextMe.exception.AppException;
import com.ahicode.TextMe.model.dto.report.ReportRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageProcessor implements SerializeMessage, DeserializeMessage {

    private final ObjectMapper objectMapper;

    @Override
    public ReportRequestDto deserializeReportRequestDto(String message) {
        try {
            return objectMapper.readValue(message, ReportRequestDto.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse incoming message: {}", message, e);
            throw new AppException("Failed to process message", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String serializeMessage(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object: {}", object, e);
            throw new AppException("Failed to serialize message", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
