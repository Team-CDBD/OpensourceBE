package com.cdbd.opensource.presentation.dto.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class TokenResponse {
    private String token;
}