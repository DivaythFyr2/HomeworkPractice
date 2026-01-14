package com.afavlad.common.event;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record OrderEvent(
    UUID orderId,
    UUID userId,
    String status,
    OffsetDateTime createdAt
) {

}