package com.afavlad.homeworkpractice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.afavlad.homeworkpractice.dto.request.CreateUserRequest;
import com.afavlad.homeworkpractice.dto.request.UpdateUserRequest;
import com.afavlad.homeworkpractice.dto.response.PageResponse;
import com.afavlad.homeworkpractice.dto.response.UserSummaryResponse;
import com.afavlad.homeworkpractice.entity.User;
import com.afavlad.homeworkpractice.exception.ConflictException;
import com.afavlad.homeworkpractice.exception.NotFoundException;
import com.afavlad.homeworkpractice.mapper.UserMapper;
import com.afavlad.homeworkpractice.repository.UserRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private UserService userService;

  private UUID userId() {
    return UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
  }

  private OffsetDateTime createdAt() {
    return OffsetDateTime.parse("2025-01-01T10:00:00+00:00");
  }

  private User user(UUID id, String name, String email, String address) {
    return User.builder()
        .id(id)
        .name(name)
        .email(email)
        .address(address)
        .createdAt(createdAt())
        .build();
  }

  private UserSummaryResponse summary(User u) {
    return UserSummaryResponse.builder()
        .id(u.getId())
        .name(u.getName())
        .email(u.getEmail())
        .address(u.getAddress())
        .createdAt(u.getCreatedAt())
        .build();
  }

  @Test
  @DisplayName("возвращает PageResponse<UserSummaryResponse>")
  void getAll_ShouldReturnPageResponse() {
    PageRequest pageable = PageRequest.of(0, 2, Sort.by(Sort.Order.desc("createdAt")));

    User u1 = user(UUID.randomUUID(), "Ivan", "ivan@mail.com", "Addr1");
    User u2 = user(UUID.randomUUID(), "Petr", "petr@mail.com", "Addr2");

    UserSummaryResponse r1 = summary(u1);
    UserSummaryResponse r2 = summary(u2);

    Page<User> userPage = new PageImpl<>(List.of(u1, u2), pageable, 5);

    when(userRepository.findAll(pageable)).thenReturn(userPage);
    when(userMapper.toSummary(u1)).thenReturn(r1);
    when(userMapper.toSummary(u2)).thenReturn(r2);

    PageResponse<UserSummaryResponse> result = userService.getAll(pageable);

    assertNotNull(result);
    assertEquals(0, result.page());
    assertEquals(2, result.size());
    assertEquals(5, result.totalElements());
    assertEquals(3, result.totalPages());
    assertFalse(result.last());

    assertEquals("createdAt,desc", result.sort());

    assertEquals(List.of(r1, r2), result.content());

    verify(userRepository, times(1)).findAll(pageable);
    verify(userMapper, times(1)).toSummary(u1);
    verify(userMapper, times(1)).toSummary(u2);
  }

  @Test
  @DisplayName("пустая страница -> корректный PageResponse")
  void getAll_WhenEmpty_ShouldReturnEmptyPageResponse() {
    PageRequest pageable = PageRequest.of(1, 10);
    Page<User> empty = new PageImpl<>(List.of(), pageable, 0);

    when(userRepository.findAll(pageable)).thenReturn(empty);

    PageResponse<UserSummaryResponse> result = userService.getAll(pageable);

    assertNotNull(result);
    assertEquals(1, result.page());
    assertEquals(10, result.size());
    assertEquals(0, result.totalElements());
    assertEquals(0, result.totalPages());
    assertTrue(result.content().isEmpty());

    verify(userRepository, times(1)).findAll(pageable);
    verifyNoInteractions(userMapper);
  }

  @Test
  @DisplayName("пользователь найден -> возвращаем summary")
  void getById_WhenUserExists_ShouldReturnSummary() {
    UUID id = userId();
    User u = user(id, "Ivan", "ivan@mail.com", "Addr");
    UserSummaryResponse expected = summary(u);

    when(userRepository.findById(id)).thenReturn(Optional.of(u));
    when(userMapper.toSummary(u)).thenReturn(expected);

    UserSummaryResponse result = userService.getById(id);

    assertNotNull(result);
    assertEquals(expected, result);

    verify(userRepository, times(1)).findById(id);
    verify(userMapper, times(1)).toSummary(u);
  }

  @Test
  @DisplayName("пользователь не найден -> NotFoundException")
  void getById_WhenUserNotFound_ShouldThrow() {
    UUID id = userId();
    when(userRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> userService.getById(id));

    verify(userRepository, times(1)).findById(id);
    verifyNoInteractions(userMapper);
  }

  @Test
  @DisplayName("успешное создание -> save + summary")
  void create_WhenEmailNotExists_ShouldSaveAndReturnSummary() {
    CreateUserRequest dto = new CreateUserRequest("Ivan", "ivan@mail.com", "Addr");

    User saved = user(UUID.randomUUID(), dto.name(), dto.email(), dto.address());
    UserSummaryResponse expected = summary(saved);

    when(userRepository.existsByEmail(dto.email())).thenReturn(false);
    when(userRepository.save(any(User.class))).thenReturn(saved);
    when(userMapper.toSummary(saved)).thenReturn(expected);

    UserSummaryResponse result = userService.create(dto);

    assertNotNull(result);
    assertEquals(expected, result);

    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    verify(userRepository, times(1)).save(captor.capture());
    User toSave = captor.getValue();

    assertEquals(dto.name(), toSave.getName());
    assertEquals(dto.email(), toSave.getEmail());
    assertEquals(dto.address(), toSave.getAddress());

    verify(userRepository, times(1)).existsByEmail(dto.email());
    verify(userMapper, times(1)).toSummary(saved);
  }

  @Test
  @DisplayName("email уже занят -> ConflictException, save не вызывается")
  void create_WhenEmailExists_ShouldThrowConflict() {
    CreateUserRequest dto = new CreateUserRequest("Ivan", "dup@mail.com", "Addr");
    when(userRepository.existsByEmail(dto.email())).thenReturn(true);

    assertThrows(ConflictException.class, () -> userService.create(dto));

    verify(userRepository, times(1)).existsByEmail(dto.email());
    verify(userRepository, never()).save(any());
    verifyNoInteractions(userMapper);
  }

  @Test
  @DisplayName("пользователь не найден -> NotFoundException")
  void update_WhenUserNotFound_ShouldThrow() {
    UUID id = userId();
    UpdateUserRequest dto = new UpdateUserRequest("NewName", null, "NewAddr");

    when(userRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> userService.update(id, dto));

    verify(userRepository, times(1)).findById(id);
    verify(userRepository, never()).save(any());
    verifyNoInteractions(userMapper);
  }

  @Test
  @DisplayName("обновляем name/address, email не трогаем -> save + summary")
  void update_WhenUserExists_UpdateNameAndAddress_ShouldSave() {
    UUID id = userId();
    User existing = user(id, "Old", "old@mail.com", "OldAddr");
    UpdateUserRequest dto = new UpdateUserRequest("NewName", null, "NewAddr");

    User saved = user(id, "NewName", "old@mail.com", "NewAddr");
    UserSummaryResponse expected = summary(saved);

    when(userRepository.findById(id)).thenReturn(Optional.of(existing));
    when(userRepository.save(existing)).thenReturn(saved);
    when(userMapper.toSummary(saved)).thenReturn(expected);

    UserSummaryResponse result = userService.update(id, dto);

    assertEquals(expected, result);
    assertEquals("NewName", existing.getName());
    assertEquals("NewAddr", existing.getAddress());
    assertEquals("old@mail.com", existing.getEmail());

    verify(userRepository, times(1)).findById(id);
    verify(userRepository, times(1)).save(existing);
    verify(userMapper, times(1)).toSummary(saved);
    verify(userRepository, never()).existsByEmail(any());
  }

  @Test
  @DisplayName("меняем email на занятый -> ConflictException, save не вызывается")
  void update_WhenEmailChangedAndAlreadyExists_ShouldThrowConflict() {
    UUID id = userId();
    User existing = user(id, "Name", "old@mail.com", "Addr");

    UpdateUserRequest dto = new UpdateUserRequest(null, "taken@mail.com", null);

    when(userRepository.findById(id)).thenReturn(Optional.of(existing));
    when(userRepository.existsByEmail(dto.email())).thenReturn(true);

    assertThrows(ConflictException.class, () -> userService.update(id, dto));

    verify(userRepository, times(1)).findById(id);
    verify(userRepository, times(1)).existsByEmail(dto.email());
    verify(userRepository, never()).save(any());
    verifyNoInteractions(userMapper);
  }

  @Test
  @DisplayName("меняем email на свободный -> save + summary")
  void update_WhenEmailChangedAndNotExists_ShouldSave() {
    UUID id = userId();
    User existing = user(id, "Name", "old@mail.com", "Addr");

    UpdateUserRequest dto = new UpdateUserRequest(null, "new@mail.com", null);

    User saved = user(id, "Name", "new@mail.com", "Addr");
    UserSummaryResponse expected = summary(saved);

    when(userRepository.findById(id)).thenReturn(Optional.of(existing));
    when(userRepository.existsByEmail(dto.email())).thenReturn(false);
    when(userRepository.save(existing)).thenReturn(saved);
    when(userMapper.toSummary(saved)).thenReturn(expected);

    UserSummaryResponse result = userService.update(id, dto);

    assertEquals(expected, result);
    assertEquals("new@mail.com", existing.getEmail());

    verify(userRepository, times(1)).findById(id);
    verify(userRepository, times(1)).existsByEmail(dto.email());
    verify(userRepository, times(1)).save(existing);
    verify(userMapper, times(1)).toSummary(saved);
  }

  @Test
  @DisplayName("пользователь не существует -> NotFoundException")
  void delete_WhenUserNotFound_ShouldThrow() {
    UUID id = userId();
    when(userRepository.existsById(id)).thenReturn(false);

    assertThrows(NotFoundException.class, () -> userService.delete(id));

    verify(userRepository, times(1)).existsById(id);
    verify(userRepository, never()).deleteById(any());
  }

  @Test
  @DisplayName("пользователь существует -> deleteById вызывается")
  void delete_WhenUserExists_ShouldDelete() {
    UUID id = userId();
    when(userRepository.existsById(id)).thenReturn(true);

    userService.delete(id);

    verify(userRepository, times(1)).existsById(id);
    verify(userRepository, times(1)).deleteById(id);
  }
}
