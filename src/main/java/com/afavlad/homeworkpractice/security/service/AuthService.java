package com.afavlad.homeworkpractice.security.service;

import com.afavlad.homeworkpractice.security.dto.LoginRequest;
import com.afavlad.homeworkpractice.security.dto.RefreshRequest;
import com.afavlad.homeworkpractice.security.dto.TokenResponse;
import com.afavlad.homeworkpractice.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final UserDetailsService userDetailsService;
  private final JwtUtils jwtUtils;
  private final LoginAttemptService loginAttemptService;

  public TokenResponse login(LoginRequest request) {
    final String username = request.username();

    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(username, request.password()));
      loginAttemptService.onSuccess(username);

      UserDetails userDetails = userDetailsService.loadUserByUsername(username);
      return new TokenResponse(jwtUtils.generateAccessToken(userDetails),
          jwtUtils.generateRefreshToken(userDetails));
    } catch (LockedException e) {
      throw e;
    } catch (BadCredentialsException e) {
      loginAttemptService.onFailure(username);
      throw e;
    }
  }

  public TokenResponse refresh(RefreshRequest request) {
    String refreshToken = request.refreshToken();
    String username = jwtUtils.extractUsername(refreshToken);
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

    if (!jwtUtils.isTokenValid(refreshToken, userDetails)) {
      throw new BadCredentialsException("Refresh token invalid");
    }
    String newAccess = jwtUtils.generateAccessToken(userDetails);

    return new TokenResponse(newAccess, refreshToken);
  }
}
