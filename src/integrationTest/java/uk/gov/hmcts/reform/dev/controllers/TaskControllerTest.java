package uk.gov.hmcts.reform.dev.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;
import java.util.Optional;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskRepository taskRepository;

    @Test
    void createTaskReturnsCreatedTask() throws Exception {
        when(taskRepository.save(any()))
            .thenAnswer(invocation -> {
                var task = invocation.getArgument(
                    0,
                    uk.gov.hmcts.reform.dev.models.Task.class
                );
                task.setId(1);
                return task;
            });

        mockMvc.perform(post("/api/tasks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                {
                                  "title": "Write tests",
                                  "description": "Add controller tests",
                                  "status": "TODO",
                                  "dueDate": "2026-11-01T12:00:00"
                                }
                                """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Write tests"))
            .andExpect(jsonPath("$.status").value("TODO"));
    }

    @Test
    void createTaskRejectsBlankTitle() throws Exception {
        mockMvc.perform(post("/api/tasks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {
                              "title": "",
                              "description": "This should be rejected",
                              "status": "TODO",
                              "dueDate": "2026-11-01T12:00:00"
                            }
                            """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Validation failed"))
            .andExpect(jsonPath("$.messages.title")
                           .value("Title must not be blank"));
    }


    @Test
    void updateTaskStatusRejectsInvalidStatus() throws Exception {
        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                    .patch("/api/tasks/1/status")
                    .param("status", "BANANA"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Validation failed"))
            .andExpect(jsonPath("$.message")
                           .value("Status must be TODO, IN_PROGRESS, or DONE"));

        verify(taskRepository, never()).save(any());
    }


    @Test
    void getTaskByIdReturnsTaskWhenFound() throws Exception {
        uk.gov.hmcts.reform.dev.models.Task task =
            new uk.gov.hmcts.reform.dev.models.Task();

        task.setId(1);
        task.setTitle("Write tests");
        task.setDescription("Test retrieving a task");
        task.setStatus("TODO");

        when(taskRepository.findById(1))
            .thenReturn(Optional.of(task));

        mockMvc.perform(get("/api/tasks/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Write tests"))
            .andExpect(jsonPath("$.status").value("TODO"));
    }


    @Test
    void getTaskByIdReturnsNotFoundWhenMissing() throws Exception {
        when(taskRepository.findById(999))
            .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/tasks/999"))
            .andExpect(status().isNotFound());
    }


    @Test
    void updateTaskStatusUpdatesExistingTask() throws Exception {
        uk.gov.hmcts.reform.dev.models.Task task =
            new uk.gov.hmcts.reform.dev.models.Task();

        task.setId(1);
        task.setTitle("Write tests");
        task.setDescription("Test updating a task");
        task.setStatus("TODO");

        when(taskRepository.findById(1))
            .thenReturn(Optional.of(task));

        when(taskRepository.save(any()))
            .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                    .patch("/api/tasks/1/status")
                    .param("status", "IN_PROGRESS"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        verify(taskRepository).save(any());
    }


    @Test
    void deleteTaskReturnsNoContentWhenTaskExists() throws Exception {
        uk.gov.hmcts.reform.dev.models.Task task =
            new uk.gov.hmcts.reform.dev.models.Task();

        task.setId(1);
        task.setTitle("Task to delete");
        task.setStatus("TODO");

        when(taskRepository.findById(1))
            .thenReturn(Optional.of(task));

        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                    .delete("/api/tasks/1"))
            .andExpect(status().isNoContent());

        verify(taskRepository).delete(task);
    }


    @Test
    void getTasksReturnsListOfTasks() throws Exception {
        uk.gov.hmcts.reform.dev.models.Task task =
            new uk.gov.hmcts.reform.dev.models.Task();

        task.setId(1);
        task.setTitle("Write tests");
        task.setStatus("TODO");

        when(taskRepository.findAll())
            .thenReturn(List.of(task));

        mockMvc.perform(get("/api/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].title").value("Write tests"));
    }


    @Test
    void createTaskAllowsMissingDescription() throws Exception {
        when(taskRepository.save(any()))
            .thenAnswer(invocation -> {
                uk.gov.hmcts.reform.dev.models.Task task =
                    invocation.getArgument(0);
                task.setId(2);
                return task;
            });

        mockMvc.perform(post("/api/tasks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {
                              "title": "Task without description",
                              "status": "TODO",
                              "dueDate": "2026-11-01T12:00:00"
                            }
                            """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.title")
                           .value("Task without description"))
            .andExpect(jsonPath("$.description").doesNotExist());
    }


    @Test
    void createTaskRejectsInvalidDueDate() throws Exception {
        mockMvc.perform(post("/api/tasks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {
                              "title": "Task with invalid date",
                              "description": "Testing date validation",
                              "status": "TODO",
                              "dueDate": "not-a-date"
                            }
                            """))
            .andExpect(status().isBadRequest());

        verify(taskRepository, never()).save(any());
    }


    @Test
    void updateTaskStatusReturnsNotFoundWhenMissing() throws Exception {
        when(taskRepository.findById(999))
            .thenReturn(Optional.empty());

        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                    .patch("/api/tasks/999/status")
                    .param("status", "DONE"))
            .andExpect(status().isNotFound());

        verify(taskRepository, never()).save(any());
    }

    @Test
    void deleteTaskReturnsNotFoundWhenMissing() throws Exception {
        when(taskRepository.findById(999))
            .thenReturn(Optional.empty());

        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                    .delete("/api/tasks/999"))
            .andExpect(status().isNotFound());

        verify(taskRepository, never()).delete(any());
    }

}
