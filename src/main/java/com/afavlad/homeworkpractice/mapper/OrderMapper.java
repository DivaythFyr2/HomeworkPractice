package com.afavlad.homeworkpractice.mapper;

import com.afavlad.homeworkpractice.dto.response.OrderDetailsResponse;
import com.afavlad.homeworkpractice.dto.response.OrderItemResponse;
import com.afavlad.homeworkpractice.dto.response.OrderSummaryResponse;
import com.afavlad.homeworkpractice.entity.Order;
import com.afavlad.homeworkpractice.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

  @Mapping(target = "userId", source = "user.id")
  OrderSummaryResponse toSummary(Order order);

  @Mapping(target = "userId", source = "user.id")
  OrderDetailsResponse toDetails(Order order);

  OrderItemResponse toItem(OrderItem item);

}
