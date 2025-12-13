package com.afavlad.homeworkpractice.dto.response;

import com.afavlad.homeworkpractice.view.Views;
import com.fasterxml.jackson.annotation.JsonView;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;

@Builder
public record OrderItemResponse(
    @JsonView(Views.OrderDetails.class)
    UUID id,

    @JsonView(Views.OrderDetails.class)
    String sku,

    @JsonView(Views.OrderDetails.class)
    String productName,

    @JsonView(Views.OrderDetails.class)
    int quantity,

    @JsonView(Views.OrderDetails.class) BigDecimal unitPrice
) {

}
