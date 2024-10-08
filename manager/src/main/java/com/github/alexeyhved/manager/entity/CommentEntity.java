package com.github.alexeyhved.manager.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@Table("comments")
public class CommentEntity {
    @Id
    private Long id;
    private Long authorId;
    private Long taskId;
    private String content;
}
