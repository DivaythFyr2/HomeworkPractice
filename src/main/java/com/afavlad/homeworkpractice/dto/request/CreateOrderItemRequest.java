package com.afavlad.homeworkpractice.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record CreateOrderItemRequest(
    @NotBlank
    String sku,
    @NotBlank
    String name,

    @NotNull
    @Positive
    Integer quantity,

    @NotNull
    @DecimalMin("0.01")
    BigDecimal unitPrice
) {

}
