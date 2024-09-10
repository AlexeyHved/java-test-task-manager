package com.github.alexeyhved.manager.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
public class TaskRequest {
    @NotBlank(message = "Must be not blank")
    @Size(min = 2, max = 20)
    private String title;
    @NotBlank(message = "Must be not blank")
    @Size(min = 5, max = 2000)
    private String description;
    @NotNull(message = "Must be not null")
    private Priority priority;

}
