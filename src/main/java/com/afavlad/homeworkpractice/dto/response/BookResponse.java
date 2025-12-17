package com.afavlad.homeworkpractice.dto.response;

import java.util.UUID;
import lombok.Builder;

@Builder
public record BookResponse(
    UUID id,
    String title,
    String author,
    Integer publicationYear
) {

}
