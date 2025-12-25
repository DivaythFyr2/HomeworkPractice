package com.afavlad.homeworkpractice.security.dto.response;

import java.time.Instant;
import lombok.Builder;

@Builder
public record ApiMessageResponse(
    String message,
    Instant timestamp,
    String path
) {

}
