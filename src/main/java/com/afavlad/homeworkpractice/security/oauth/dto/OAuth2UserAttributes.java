package com.afavlad.homeworkpractice.security.oauth.dto;

public record OAuth2UserAttributes(
    String providerUserId,
    String name,
    String email
) {

}
