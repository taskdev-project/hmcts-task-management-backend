package uk.gov.hmcts.reform.dev.controllers;

import jakarta.validation.Valid;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import uk.gov.hmcts.reform.dev.models.ApiError;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;

import static org.springframework.http.ResponseEntity.ok;

@RestController
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Operation(summary = "Create a new task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Task created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid task data")
    })
    @PostMapping(value = "/api/tasks", consumes = "application/json",
        produces = "application/json")
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        task.setId(null);
        Task savedTask = taskRepository.save(task);
        return ResponseEntity.status(201).body(savedTask);
    }

    @Operation(summary = "Retrieve all tasks")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully")
    })
    @GetMapping(value = "/api/tasks", produces = "application/json")
    public ResponseEntity<List<Task>> getTasks() {
        return ok(taskRepository.findAll());
    }

    @Operation(summary = "Retrieve a task by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @GetMapping(value = "/api/tasks/{id}", produces = "application/json")
    public ResponseEntity<Task> getTaskById(@PathVariable int id) {
        return taskRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update a task's status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status"),
        @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PatchMapping("/api/tasks/{id}/status")
    public ResponseEntity<?> updateTaskStatus(
        @PathVariable int id,
        @RequestParam String status) {

        if (!status.matches("TODO|IN_PROGRESS|DONE")) {
            ApiError error = new ApiError(
                400,
                "Validation failed",
                "Status must be TODO, IN_PROGRESS, or DONE"
            );
            return ResponseEntity.badRequest().body(error);
        }

        return taskRepository.findById(id)
            .map(task -> {
                task.setStatus(status);
                return ResponseEntity.ok(taskRepository.save(task));
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a task by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @DeleteMapping("/api/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable int id) {
        return taskRepository.findById(id)
            .map(task -> {
                taskRepository.delete(task);
                return ResponseEntity.noContent().<Void>build();
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
