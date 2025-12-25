package com.afavlad.homeworkpractice.security.oauth;

import com.afavlad.homeworkpractice.security.model.AppUser;
import com.afavlad.homeworkpractice.security.model.AuthProvider;
import com.afavlad.homeworkpractice.security.model.Role;
import com.afavlad.homeworkpractice.security.oauth.dto.OAuth2UserAttributes;
import com.afavlad.homeworkpractice.security.repository.AppUserRepository;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

  private static final String NAME_ATTRIBUTE_KEY = "sub";

  private final AppUserRepository appUserRepository;
  private final RoleResolver roleResolver;

  private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

  @Override
  @Transactional
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oauth2User = delegate.loadUser(userRequest);

    OAuth2UserAttributes attrs = extractGoogleAttrs(oauth2User.getAttributes());
    Role role = roleResolver.resolve(attrs.email());

    AppUser saved = upsertUser(AuthProvider.GOOGLE, attrs, role);

    log.info("OAUTH2 login: userId={} email={} role={}", saved.getId(), saved.getEmail(),
        saved.getRole());

    List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + saved.getRole().name()));
    return new DefaultOAuth2User(authorities, oauth2User.getAttributes(), NAME_ATTRIBUTE_KEY);
  }

  private OAuth2UserAttributes extractGoogleAttrs(Map<String, Object> attributes) {
    Object sub = attributes.get("sub");
    if (sub == null) {
      throw new IllegalStateException("Missing required attribute 'sub' from OAuth2 provider");
    }

    String providerUserId = String.valueOf(sub);
    String name = String.valueOf(attributes.getOrDefault("name", "unknown"));
    String email = (String) attributes.get("email");

    return new OAuth2UserAttributes(providerUserId, name, email);
  }

  private AppUser upsertUser(AuthProvider provider, OAuth2UserAttributes attrs, Role role) {
    AppUser entity = appUserRepository.findByProviderAndProviderUserId(provider, attrs.providerUserId())
        .map(existing -> {
          existing.setName(attrs.name());
          existing.setEmail(attrs.email());
          existing.setRole(role);
          return existing;
        })
        .orElseGet(() -> AppUser.builder()
            .provider(provider)
            .providerUserId(attrs.providerUserId())
            .name(attrs.name())
            .email(attrs.email())
            .role(role)
            .build());

    return appUserRepository.save(entity);
  }
}
