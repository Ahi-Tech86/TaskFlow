package com.ahicode.TextMe.service.producer;

import com.ahicode.TextMe.config.security.accessControl.PermissionChecker;
import com.ahicode.TextMe.exception.AppException;
import com.ahicode.TextMe.model.entity.ProjectMemberEntity;
import com.ahicode.TextMe.model.enums.Action;
import com.ahicode.TextMe.repository.ProjectMemberRepository;
import com.ahicode.TextMe.service.ProjectValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportProducer {

    private final RabbitTemplate rabbitTemplate;
    private final PermissionChecker permissionChecker;
    private final ProjectMemberRepository memberRepository;
    private final ProjectValidationService validationService;

    public void processRequest(Long projectId, Long userId) {
        validationService.isProjectExistsById(projectId);
        ProjectMemberEntity user = memberRepository.getProjectMemberEntityByProjectIdAndUserId(userId, projectId);

        boolean canGetReport = permissionChecker.hasPermission(user, "reports", Action.GET_REPORT, null);
        if (!canGetReport) {
            log.error("Attempt to change resource with not sufficient project permissions");
            throw new AppException("User does not have sufficient permissions", HttpStatus.FORBIDDEN);
        }

        rabbitTemplate.convertAndSend("exchanger", "reports.routing.key", projectId);
        log.info("Send request to report from {}", projectId);
    }
}
