package com.afavlad.homeworkpractice.api.error;

import java.time.Instant;
import lombok.Builder;

@Builder
public record ErrorResponseDto(
    String message,
    int status,
    Instant timestamp
) {

}
