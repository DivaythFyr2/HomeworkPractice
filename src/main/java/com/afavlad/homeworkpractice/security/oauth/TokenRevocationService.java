package com.afavlad.homeworkpractice.security.oauth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenRevocationService {

  private static final String GOOGLE_REVOKE_URL = "https://oauth2.googleapis.com/revoke";

  private final OAuth2AuthorizedClientService authorizedClientService;
  private final WebClient webClient;

  public void revoke(HttpServletRequest request, Authentication authentication) {
    String principal = authentication != null ? authentication.getName() : "anonymous";
    log.info("ACCESS_REVOKE requested principal={}", principal);

    if (!(authentication instanceof OAuth2AuthenticationToken oauthToken)) {
      log.info("ACCESS_REVOKE skipped: not OAuth2AuthenticationToken");
      return;
    }

    String registrationId = oauthToken.getAuthorizedClientRegistrationId();
    String principalName = oauthToken.getName();

    OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(registrationId, principalName);

    if (client == null || client.getAccessToken() == null) {
      log.info("ACCESS_REVOKE skipped: no authorized client/access token");
      return;
    }

    String accessTokenValue = client.getAccessToken().getTokenValue();

    try {
      webClient.post()
          .uri(GOOGLE_REVOKE_URL)
          .contentType(MediaType.APPLICATION_FORM_URLENCODED)
          .bodyValue("token=" + accessTokenValue)
          .retrieve()
          .toBodilessEntity()
          .block();

      log.info("ACCESS_REVOKE success provider=GOOGLE principal={}", principal);

      authorizedClientService.removeAuthorizedClient(registrationId, principalName);

    } catch (Exception e) {
      log.warn("ACCESS_REVOKE failed provider=GOOGLE principal={} error={}", principal, e.toString());
    }
  }
}
