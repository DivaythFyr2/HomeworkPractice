package com.afavlad.homeworkpractice.dto.response;

import com.afavlad.homeworkpractice.view.Views;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record UserSummaryResponse(
    @JsonView(Views.UserSummary.class)
    UUID id,

    @JsonView(Views.UserSummary.class)
    String name,

    @JsonView(Views.UserSummary.class)
    String email,

    @JsonView(Views.UserSummary.class)
    String address,

    @JsonView(Views.UserSummary.class)
    @JsonProperty("created_at")
    OffsetDateTime createdAt
) {

}
