package com.ahicode.TextMe.service.generator;

import com.ahicode.TextMe.exception.AppException;
import com.ahicode.TextMe.model.dto.report.ReportDto;
import com.ahicode.TextMe.model.dto.report.UserProjectStats;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Map;

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

                // Заголовок
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("Project Report");
                contentStream.newLineAtOffset(0, -20);

                // Информация о проекте
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.showText("Project Information:");
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Name: " + report.getProjectName());
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Description: " + report.getProjectDescription());
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Owner: " + report.getProjectOwnerNickname());
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Created At: " + report.getProjectCreateAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Start Date: " + report.getProjectStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
                contentStream.newLineAtOffset(0, -30);

                // Текущие задачи
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.showText("Current Tasks:");
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("All: " + report.getAllCurrentTasks());
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("In Todo: " + report.getCurrentTasksInTodoStatus());
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("In Progress: " + report.getCurrentTasksInProgressStatus());
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Needs Approval: " + report.getCurrentTasksInNeedsApprovalStatus());
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Low Priority: " + report.getCurrentTasksWithLowPriority());
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Medium Priority: " + report.getCurrentTasksWithMediumPriority());
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("High Priority: " + report.getCurrentTasksWithHighPriority());
                contentStream.newLineAtOffset(0, -30);

                // Завершенные задачи
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.showText("Completed Tasks:");
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Successful: " + report.getSuccessfulCompletedTasksInProject());
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Unsuccessful: " + report.getUnsuccessfulCompletedTasksInProject());
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("All: " + report.getAllCompletedTasksInProject());
                contentStream.newLineAtOffset(0, -30);

                // Таблица участников проекта
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.showText("Project Members:");
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(0, -20);

                // Заголовки таблицы
                contentStream.showText("Nickname");
                contentStream.newLineAtOffset(100, 0);
                contentStream.showText("Role");
                contentStream.newLineAtOffset(100, 0);
                contentStream.showText("Joined At");
                contentStream.newLineAtOffset(100, 0);
                contentStream.showText("Current Tasks");
                contentStream.newLineAtOffset(100, 0);
                contentStream.showText("Completed Tasks");
                contentStream.newLineAtOffset(100, 0);
                contentStream.showText("Completed Tasks Percentage");
                contentStream.newLineAtOffset(0, -15);

                // Данные таблицы
                for (Map.Entry<String, UserProjectStats> entry : report.getProjectMembersStatsMap().entrySet()) {
                    UserProjectStats stats = entry.getValue();
                    contentStream.showText(entry.getKey());
                    contentStream.newLineAtOffset(50, 0);
                    contentStream.showText(stats.getRole().toString());
                    contentStream.newLineAtOffset(50, 0);
                    contentStream.showText(stats.getJoinedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    contentStream.newLineAtOffset(50, 0);
                    contentStream.showText(stats.getCurrentTasks().toString());
                    contentStream.newLineAtOffset(50, 0);
                    contentStream.showText(stats.getCompletedTasks().toString());
                    contentStream.newLineAtOffset(50, 0);
                    contentStream.showText(stats.getCompletedTasksPercentage() != null ? stats.getCompletedTasksPercentage().toString() : "0");
                    contentStream.newLineAtOffset(-500, -15);
                }

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
