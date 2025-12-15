package com.afavlad.homeworkpractice.dto.response;

import com.afavlad.homeworkpractice.view.Views;
import com.fasterxml.jackson.annotation.JsonView;
import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

@Builder
public record PageResponse<T>(
    @JsonView({Views.UserSummary.class, Views.OrderSummary.class})
    List<T> content,

    @JsonView({Views.UserSummary.class, Views.OrderSummary.class})
    int page,

    @JsonView({Views.UserSummary.class, Views.OrderSummary.class})
    int size,

    @JsonView({Views.UserSummary.class, Views.OrderSummary.class})
    long totalElements,

    @JsonView({Views.UserSummary.class, Views.OrderSummary.class})
    int totalPages,

    @JsonView({Views.UserSummary.class, Views.OrderSummary.class})
    boolean last,

    @JsonView({Views.UserSummary.class, Views.OrderSummary.class})
    String sort
) {

  public static <T> PageResponse<T> of(Page<T> page) {
    return PageResponse.<T>builder()
        .content(page.getContent())
        .page(page.getNumber())
        .size(page.getSize())
        .totalElements(page.getTotalElements())
        .totalPages(page.getTotalPages())
        .last(page.isLast())
        .sort(formatSort(page.getSort()))
        .build();
  }

  private static String formatSort(Sort sort) {
    if (sort == null || sort.isUnsorted()) return "";
    return sort.stream()
        .map(o -> o.getProperty() + "," + o.getDirection().name().toLowerCase())
        .reduce((a, b) -> a + ";" + b)
        .orElse("");
  }
}
