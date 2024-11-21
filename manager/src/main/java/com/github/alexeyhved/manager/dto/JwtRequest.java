package com.github.alexeyhved.manager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
public class JwtRequest {
    @NotBlank(message = "Invalid login")
    @Length(min = 2, max = 64, message = "Invalid login")
    @Email(message = "Not valid email")
    private String login;
    @NotBlank(message = "Invalid password")
    @Length(min = 4, max = 64, message = "Invalid password")
    private String pass;
}
