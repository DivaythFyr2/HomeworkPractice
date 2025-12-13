package com.afavlad.homeworkpractice.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(
    @NotNull
    UUID userId,

    @NotNull
    @Size(min = 1)
    List<@Valid CreateOrderItemRequest> items
) {

}
