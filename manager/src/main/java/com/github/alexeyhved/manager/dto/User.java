package com.github.alexeyhved.manager.dto;

import com.github.alexeyhved.manager.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class User {
    private Long id;
    private String login;
    private Role role;
}
