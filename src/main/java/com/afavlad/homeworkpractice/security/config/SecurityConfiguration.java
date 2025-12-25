package com.afavlad.homeworkpractice.security.config;

import com.afavlad.homeworkpractice.security.handlers.CustomLogoutSuccessHandler;
import com.afavlad.homeworkpractice.security.handlers.RestAccessDeniedHandler;
import com.afavlad.homeworkpractice.security.handlers.RestAuthenticationEntryPoint;
import com.afavlad.homeworkpractice.security.oauth.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

  private final CustomOAuth2UserService customOAuth2UserService;
  private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
  private final RestAccessDeniedHandler restAccessDeniedHandler;
  private final CustomLogoutSuccessHandler customLogoutSuccessHandler;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/error").permitAll()
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated())
        .oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(u -> u.userService(customOAuth2UserService))
            .defaultSuccessUrl("/api/me", true))
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(restAuthenticationEntryPoint)
            .accessDeniedHandler(restAccessDeniedHandler))
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessHandler(customLogoutSuccessHandler)
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID"));

    return http.build();
  }

}
