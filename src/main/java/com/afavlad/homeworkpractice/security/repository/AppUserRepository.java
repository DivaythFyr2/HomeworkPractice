package com.afavlad.homeworkpractice.security.repository;

import com.afavlad.homeworkpractice.security.model.AppUser;
import com.afavlad.homeworkpractice.security.model.AuthProvider;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
  Optional<AppUser> findByProviderAndProviderUserId(AuthProvider authProvider, String providerUserId);
}
