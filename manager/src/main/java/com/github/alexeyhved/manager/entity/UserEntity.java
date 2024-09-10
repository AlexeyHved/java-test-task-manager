package com.github.alexeyhved.manager.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@NoArgsConstructor
@Getter
@Setter
@Table("users")
public class UserEntity implements Persistable<Long> {
    @Id
    private Long id;

    private String login;

    @Transient
    private boolean isNewEntry = true;

    @Override
    public boolean isNew() {
        return isNewEntry;
    }
}
