package com.github.alexeyhved.manager.dto;

import com.github.alexeyhved.manager.entity.Role;

public class UserAdmin extends User {
    public UserAdmin(Long id, String login, Role role) {
        super(id, login, role);
    }
}
