package com.afavlad.homeworkpractice.security.dto.response;

import lombok.Builder;

@Builder
public record AdminPingResponse(
    String status,
    String scope
) {

}
