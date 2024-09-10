package com.github.alexeyhved.manager.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RefreshJwtRequest {

    private String refreshToken;

}
