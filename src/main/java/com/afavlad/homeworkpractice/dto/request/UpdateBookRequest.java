package com.afavlad.homeworkpractice.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UpdateBookRequest(
    @NotBlank
    String title,

    @NotBlank
    String author,

    @NotNull
    @Min(0)
    @Max(2100)
    Integer publicationYear
) {

}
