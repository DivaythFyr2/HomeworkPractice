package com.afavlad.homeworkpractice.api.error;

import java.time.OffsetDateTime;
import lombok.Builder;

@Builder
public record ErrorResponseDto(
    OffsetDateTime timestamp,
    int status,
    String error,
    String message,
    String path
) {

}
