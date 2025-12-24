package com.afavlad.homeworkpractice.security.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(
    @NotBlank
    String refreshToken
) {

}
