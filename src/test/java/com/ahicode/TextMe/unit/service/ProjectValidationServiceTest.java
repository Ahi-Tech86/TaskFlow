package com.ahicode.TextMe.unit.service;

import com.ahicode.TextMe.exception.AppException;
import com.ahicode.TextMe.model.entity.ProjectEntity;
import com.ahicode.TextMe.repository.ProjectRepository;
import com.ahicode.TextMe.service.impl.ProjectValidationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectValidationServiceTest {

    @Mock
    private ProjectRepository repository;

    @InjectMocks
    private ProjectValidationServiceImpl service;

    @Test
    void testIsProjectExistsById_ProjectExists() {
        Long projectId = 1L;
        ProjectEntity expectedProject = new ProjectEntity();
        expectedProject.setId(projectId);

        when(repository.findById(projectId)).thenReturn(Optional.of(expectedProject));

        ProjectEntity actualProject = service.isProjectExistsById(projectId);

        assertThat(actualProject).isEqualTo(expectedProject);
    }

    @Test
    void testIsProjectExistsById_ProjectDoesNotExist() {
        Long projectId = 1L;

        when(repository.findById(projectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.isProjectExistsById(projectId))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(String.format("Project with id %s doesn't exists", projectId))
                .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND);
    }
}
