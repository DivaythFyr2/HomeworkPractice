package com.afavlad.homeworkpractice.dto.request;

import jakarta.validation.constraints.Email;

public record UpdateUserRequest(

    String name,

    @Email
    String email,

    String address
) {

}
