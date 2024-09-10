package com.github.alexeyhved.manager.entity;

import com.github.alexeyhved.manager.dto.Priority;
import com.github.alexeyhved.manager.dto.Status;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("tasks")
public class TaskEntity {
    @Id
    private Long id;
    private Long authorId;
    private String title;
    private String description;
    private Status status;
    private Priority priority;
}
