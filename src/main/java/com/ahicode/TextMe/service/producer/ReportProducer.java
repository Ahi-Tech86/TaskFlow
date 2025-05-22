package com.ahicode.TextMe.service.producer;

import com.ahicode.TextMe.config.security.accessControl.PermissionChecker;
import com.ahicode.TextMe.exception.AppException;
import com.ahicode.TextMe.model.dto.report.ReportRequestDto;
import com.ahicode.TextMe.model.entity.ProjectMemberEntity;
import com.ahicode.TextMe.model.enums.Action;
import com.ahicode.TextMe.repository.ProjectMemberRepository;
import com.ahicode.TextMe.service.ProjectValidationService;
import com.ahicode.TextMe.service.factory.report.ReportRequestDtoFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportProducer {

    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;
    private final ReportRequestDtoFactory factory;
    private final PermissionChecker permissionChecker;
    private final ProjectMemberRepository memberRepository;
    private final ProjectValidationService validationService;

    public void processRequest(Long projectId, Long userId) {
        ProjectMemberEntity projectMember = validateAndGetProjectMember(projectId, userId);
        ReportRequestDto requestDto = factory.makeReportRequestDto(projectId, projectMember.getUser().getEmail());
        sendReportRequest(requestDto);
    }

    private ProjectMemberEntity validateAndGetProjectMember(Long projectId, Long userId) {
        validationService.isProjectExistsById(projectId);
        ProjectMemberEntity projectMember = memberRepository.getProjectMemberEntityByProjectIdAndUserId(userId, projectId);

        if (!permissionChecker.hasPermission(projectMember, "reports", Action.GET_REPORT, null)) {
            log.error("Attempt to change resource with not sufficient project permissions");
            throw new AppException("User does not have sufficient permissions", HttpStatus.FORBIDDEN);
        }

        return projectMember;
    }

    private void sendReportRequest(ReportRequestDto requestDto) {
        try {
            String jsonRequestBody = objectMapper.writeValueAsString(requestDto);
            rabbitTemplate.convertAndSend("exchanger", "reports.routing.key", jsonRequestBody);
            log.info("Send request to report from {}", jsonRequestBody);

        } catch (JsonProcessingException e) {
            log.error("An error occurred while serializing and sending the request {}", requestDto.toString());
            throw new AppException("An internal server error occurred, please try again", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
