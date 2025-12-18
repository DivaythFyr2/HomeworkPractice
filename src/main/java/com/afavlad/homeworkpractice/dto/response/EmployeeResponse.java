package com.afavlad.homeworkpractice.dto.response;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;

@Builder
public record EmployeeResponse(
    UUID id,
    String firstName,
    String lastName,
    String position,
    BigDecimal salary,
    String departmentName
) {

}
