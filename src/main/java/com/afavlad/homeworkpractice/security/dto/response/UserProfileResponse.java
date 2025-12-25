package com.afavlad.homeworkpractice.security.dto.response;

import com.afavlad.homeworkpractice.security.model.AuthProvider;
import com.afavlad.homeworkpractice.security.model.Role;
import java.util.UUID;
import lombok.Builder;

@Builder
public record UserProfileResponse(
    UUID userId,
    AuthProvider provider,
    String providerUserId,
    String name,
    String email,
    Role role
) {

}
