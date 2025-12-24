package com.afavlad.homeworkpractice.security.exception;

import java.time.Instant;
import lombok.Builder;

@Builder
public record AuthErrorResponseDto(
    String message,
    int status,
    Instant timestamp
)
{

}
