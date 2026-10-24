package uk.gov.hmcts.reform.dev.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
        MethodArgumentNotValidException exception) {

        Map<String, String> errors = exception.getBindingResult()
            .getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                error -> error.getField(),
                error -> error.getDefaultMessage(),
                (first, second) -> first
            ));

        Map<String, Object> body = Map.of(
            "status", HttpStatus.BAD_REQUEST.value(),
            "error", "Validation failed",
            "messages", errors
        );

        return ResponseEntity.badRequest().body(body);
    }
}
