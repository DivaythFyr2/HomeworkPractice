package com.afavlad.homeworkpractice.security.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public record AppSecurityProperties(
    List<String> adminEmails
) {

}
