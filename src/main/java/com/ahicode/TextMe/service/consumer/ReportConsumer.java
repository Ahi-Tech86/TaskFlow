package com.ahicode.TextMe.service.consumer;

import com.ahicode.TextMe.model.dto.report.ReportDto;
import com.ahicode.TextMe.model.dto.report.ReportRequestDto;
import com.ahicode.TextMe.service.EmailService;
import com.ahicode.TextMe.service.generator.PdfGenerator;
import com.ahicode.TextMe.service.processor.DeserializeMessage;
import com.ahicode.TextMe.service.other.ProjectDataCollector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportConsumer {

    private final PdfGenerator pdfGenerator;
    private final EmailService emailService;
    private final DeserializeMessage deserializer;
    private final ProjectDataCollector dataCollector;

    @RabbitListener(queues = "reports_queue")
    public void receiveMessage(String message) {
        ReportRequestDto requestDto = deserializer.deserializeReportRequestDto(message);
        Long projectId = requestDto.getProjectId();
        String email = requestDto.getEmail();

        ReportDto report = dataCollector.collectProjectData(projectId);
        ByteArrayOutputStream outputStream = pdfGenerator.generatePdf(report);

        emailService.sendReportForAllTime(email, report.getProjectName(), outputStream.toByteArray());
    }
}
