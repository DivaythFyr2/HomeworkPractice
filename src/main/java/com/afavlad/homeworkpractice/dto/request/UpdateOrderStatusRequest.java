package com.afavlad.homeworkpractice.dto.request;

import com.afavlad.homeworkpractice.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
    @NotNull
    OrderStatus status
) {

}
