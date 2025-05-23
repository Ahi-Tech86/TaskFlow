package com.ahicode.TextMe.service.producer;

import com.ahicode.TextMe.config.security.accessControl.PermissionChecker;
import com.ahicode.TextMe.exception.AppException;
import com.ahicode.TextMe.model.dto.report.ReportRequestDto;
import com.ahicode.TextMe.model.entity.ProjectMemberEntity;
import com.ahicode.TextMe.model.enums.Action;
import com.ahicode.TextMe.repository.ProjectMemberRepository;
import com.ahicode.TextMe.service.ProjectValidationService;
import com.ahicode.TextMe.service.factory.report.ReportRequestDtoFactory;
import com.ahicode.TextMe.service.processor.SerializeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportProducer {

    private final SerializeMessage serializer;
    private final RabbitTemplate rabbitTemplate;
    private final ReportRequestDtoFactory factory;
    private final PermissionChecker permissionChecker;
    private final ProjectMemberRepository memberRepository;
    private final ProjectValidationService validationService;

    public void processRequest(Long projectId, Long userId) {
        ProjectMemberEntity projectMember = validateAndGetProjectMember(projectId, userId);
        ReportRequestDto requestDto = factory.makeReportRequestDto(projectId, projectMember.getUser().getEmail());
        String jsonMessage = serializer.serializeMessage(requestDto);
        rabbitTemplate.convertAndSend("exchanger", "reports.routing.key", jsonMessage);
        log.info("Send request to report from {}", jsonMessage);
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
}
