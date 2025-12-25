package com.afavlad.homeworkpractice.security.api.error;

import java.time.Instant;
import lombok.Builder;

@Builder
public record ErrorResponseDto(
    String message,
    int status,
    String path,
    Instant timestamp
) {

}
