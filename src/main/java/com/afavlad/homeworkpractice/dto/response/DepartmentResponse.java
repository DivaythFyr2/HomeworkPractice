package com.afavlad.homeworkpractice.dto.response;

import java.util.UUID;
import lombok.Builder;

@Builder
public record DepartmentResponse(
    UUID id,
    String name
) {

}
