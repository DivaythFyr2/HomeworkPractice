package com.afavlad.homeworkpractice.security.dto;

public record TokenResponse(
    String accessToken,
    String refreshToken
) {

}
