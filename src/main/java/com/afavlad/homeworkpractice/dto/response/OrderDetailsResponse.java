package com.afavlad.homeworkpractice.dto.response;

import com.afavlad.homeworkpractice.enums.OrderStatus;
import com.afavlad.homeworkpractice.view.Views;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record OrderDetailsResponse(
    @JsonView(Views.OrderSummary.class)
    UUID id,

    @JsonView(Views.OrderSummary.class)
    UUID userId,

    @JsonView(Views.OrderSummary.class)
    BigDecimal totalAmount,

    @JsonView(Views.OrderDetails.class)
    OrderStatus status,

    @JsonView(Views.OrderSummary.class)
    @JsonProperty("created_at")
    OffsetDateTime createdAt,

    @JsonView(Views.OrderDetails.class)
    List<OrderItemResponse> items
) {

}
