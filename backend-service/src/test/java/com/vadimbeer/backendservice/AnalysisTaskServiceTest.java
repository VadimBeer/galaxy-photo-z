package com.vadimbeer.backendservice;

import com.vadimbeer.backendservice.DTO.TaskCreatedEvent;
import com.vadimbeer.backendservice.jpa.AnalysisTaskRepository;
import com.vadimbeer.backendservice.models.AnalysisTask;
import com.vadimbeer.backendservice.models.FilesStorage;
import com.vadimbeer.backendservice.models.ModelVersion;
import com.vadimbeer.backendservice.models.User;
import com.vadimbeer.backendservice.service.AnalysisTaskService;
import com.vadimbeer.backendservice.service.ModelVersionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalysisTaskServiceTest {

    @Mock
    private AnalysisTaskRepository repository;
    @Mock
    private ModelVersionService modelVersionService;
    @Mock
    private ApplicationEventPublisher event;

    @InjectMocks
    private AnalysisTaskService service;

    @Test
    void create_savesPendingTask_andPublishesEventAfterSave() {
        // given
        ModelVersion modelVersion = mock(ModelVersion.class);
        when(modelVersion.getId()).thenReturn(3);
        when(modelVersionService.getModelVersionById(3)).thenReturn(modelVersion);

        User user = mock(User.class);
        when(user.getId()).thenReturn(7L);

        FilesStorage file = mock(FilesStorage.class);
        when(file.getUser()).thenReturn(user);
        when(file.getObjectKey()).thenReturn("photos/galaxy.jpg");

        AnalysisTask saved = mock(AnalysisTask.class);
        when(saved.getId()).thenReturn(42L);
        when(repository.save(any(AnalysisTask.class))).thenReturn(saved);

        // when
        AnalysisTask result = service.create(3, file);

        // then
        assertThat(result).isSameAs(saved);

        ArgumentCaptor<AnalysisTask> captor = ArgumentCaptor.forClass(AnalysisTask.class);
        InOrder inOrder = inOrder(repository, event);
        inOrder.verify(repository).save(captor.capture());
        inOrder.verify(event).publishEvent(new TaskCreatedEvent(42L, 7L, 3, "photos/galaxy.jpg"));

        assertThat(captor.getValue().getState()).isEqualTo(AnalysisTask.State.PENDING);
    }

    @Test
    void create_whenModelVersionNotFound_doesNotSaveAndDoesNotPublish() {
        FilesStorage file = mock(FilesStorage.class);
        when(modelVersionService.getModelVersionById(99))
                .thenThrow(new RuntimeException("model version not found"));

        assertThatThrownBy(() -> service.create(99, file))
                .isInstanceOf(RuntimeException.class);

        verifyNoInteractions(repository, event);
    }
}