package com.ahicode.TextMe.service.consumer;

import com.ahicode.TextMe.exception.AppException;
import com.ahicode.TextMe.model.dto.project.ProjectDto;
import com.ahicode.TextMe.model.dto.report.ReportRequestDto;
import com.ahicode.TextMe.model.entity.ProjectEntity;
import com.ahicode.TextMe.model.enums.CompletionTaskStatus;
import com.ahicode.TextMe.model.enums.ProjectRole;
import com.ahicode.TextMe.model.enums.TaskPriority;
import com.ahicode.TextMe.model.enums.TaskStatus;
import com.ahicode.TextMe.repository.*;
import com.ahicode.TextMe.service.EmailService;
import com.ahicode.TextMe.service.ProjectValidationService;
import com.ahicode.TextMe.service.factory.project.ProjectDtoFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportConsumer {

    private final EmailService emailService;
    private final ObjectMapper objectMapper;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final CompletedTaskRepository completedTaskRepository;
    private final ProjectValidationService projectValidationService;

    @RabbitListener(queues = "reports_queue")
    public void receiveMessage(String message) {
        Long projectId;
        String email;

        try {
            ReportRequestDto requestDto = objectMapper.readValue(message, ReportRequestDto.class);
            projectId = requestDto.getProjectId();
            email = requestDto.getEmail();

        } catch (JsonProcessingException e) {
            log.error("Failed to parse incoming message: {}", message, e);
            throw new AppException("Failed to process message", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // PROJECT INFO
        ProjectEntity project = projectValidationService.isProjectExistsById(projectId);
        String projectOwner = userRepository.findById(project.getOwnerId()).get().getNickname();


        // DATA ABOUT TASS
        Map<CompletionTaskStatus, Long> taskCount = new EnumMap<>(CompletionTaskStatus.class);
        List<Object[]> list = completedTaskRepository.countTasksByCompletionStatus();
        for (Object[] result : list) {
            CompletionTaskStatus status = (CompletionTaskStatus) result[0];
            Long quantity = (Long) result[1];
        }
        Long successfulTaskCount = taskCount.getOrDefault(CompletionTaskStatus.SUCCESSFUL, 0L);
        Long unsuccessfulTaskCount = taskCount.getOrDefault(CompletionTaskStatus.UNSUCCESSFUL, 0L);
        Long quantityTasksInProgress = taskRepository.countInProgressTasksByProjectId(projectId);
        Long quantityTasks = taskRepository.countTasksByProjectId(projectId);

        Map<TaskStatus, Long> tasksCountByStatus = new EnumMap<>(TaskStatus.class);
        List<Object[]> list2 = taskRepository.countTasksByStatus(projectId);
        for (Object[] result : list2) {
            TaskStatus status = (TaskStatus) result[0];
            Long quantity = (Long) result[1];
        }
        Long todoTaskQuantity = tasksCountByStatus.getOrDefault(TaskStatus.TO_DO, 0L);
        Long inProgressTaskQuantity = tasksCountByStatus.getOrDefault(TaskStatus.IN_PROGRESS, 0L);
        Long needsToApprovalTaskQuantity = tasksCountByStatus.getOrDefault(TaskStatus.NEEDS_APPROVAL, 0L);


        Map<TaskPriority, Long> tasksCountByPriority = new EnumMap<>(TaskPriority.class);
        List<Object[]> list3 = taskRepository.countTasksByPriority(projectId);
        for (Object[] result : list3) {
            TaskPriority priority = (TaskPriority) result[0];
            Long quantity = (Long) result[1];
        }
        Long lowPriorityTasksQuantity = tasksCountByPriority.getOrDefault(TaskPriority.LOW, 0L);
        Long mediumPriorityTasksQuantity = tasksCountByPriority.getOrDefault(TaskPriority.MEDIUM, 0L);
        Long highPriorityTasksQuantity = tasksCountByPriority.getOrDefault(TaskPriority.HIGH, 0L);

        Long quantityOfProjectMembers = projectMemberRepository.countMembersInProject(projectId);

        List<Object[]> list4 = projectMemberRepository.findProjectMembersWithTaskStatus(projectId);
        for (Object[] result : list4) {
            String nickname = (String) result[0];
            ProjectRole role = ProjectRole.fromName((String) result[1]);
            LocalDateTime joinedAt = ((Timestamp) result[2]).toLocalDateTime();
            Long currentTasks = (Long) result[3];
            Long completedTasks = (Long) result[4];
            BigDecimal completedTasksPercentage = (BigDecimal) result[5];

            System.out.printf("%s role: %s, joinedAt: %s, currentTasks: %s, completedTasks: %s, completedTasksPercentage: %s %n",
                    nickname, role.toString(), joinedAt.toString(), currentTasks, completedTasks, completedTasksPercentage
            );
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (PDDocument pdfDocument = new PDDocument()) {
            PDPage page = new PDPage();
            pdfDocument.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(pdfDocument, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("Hello world");
                contentStream.endText();
            }

            pdfDocument.save(outputStream);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        emailService.sendEmailWithAttachment(
                email,
                "Project Report",
                "Please find the attached project report",
                outputStream.toByteArray(),
                "report.pdf"
        );
    }
}
