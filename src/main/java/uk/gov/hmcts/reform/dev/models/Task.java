package uk.gov.hmcts.reform.dev.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Future;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Title must not be blank")
    private String title;

    private String description;

    @NotBlank(message = "Status must not be blank")
    @Pattern(
        regexp = "TODO|IN_PROGRESS|DONE",
        message = "Status must be TODO, IN_PROGRESS, or DONE"
    )
    private String status;

    @Future(message = "Due date must be in the future")
    private LocalDateTime dueDate;

}
