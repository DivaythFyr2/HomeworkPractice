package com.afavlad.homeworkpractice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.afavlad.homeworkpractice.dto.request.CreateUserRequest;
import com.afavlad.homeworkpractice.dto.request.UpdateUserRequest;
import com.afavlad.homeworkpractice.dto.response.OrderDetailsResponse;
import com.afavlad.homeworkpractice.dto.response.UserDetailsResponse;
import com.afavlad.homeworkpractice.dto.response.UserSummaryResponse;
import com.afavlad.homeworkpractice.entity.Order;
import com.afavlad.homeworkpractice.entity.User;
import com.afavlad.homeworkpractice.exception.ConflictException;
import com.afavlad.homeworkpractice.exception.NotFoundException;
import com.afavlad.homeworkpractice.mapper.UserMapper;
import com.afavlad.homeworkpractice.repository.OrderRepository;
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

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private OrderRepository orderRepository;

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

  private User user(UUID id, String name, String email, String address, List<Order> orders) {
    return User.builder()
        .id(id)
        .name(name)
        .email(email)
        .address(address)
        .createdAt(createdAt())
        .orders(orders)
        .build();
  }

  private UserSummaryResponse summaryFrom(User u) {
    return UserSummaryResponse.builder()
        .id(u.getId())
        .name(u.getName())
        .email(u.getEmail())
        .address(u.getAddress())
        .createdAt(u.getCreatedAt())
        .build();
  }

  private UserDetailsResponse detailsFrom(User u, List<OrderDetailsResponse> orders) {
    return UserDetailsResponse.builder()
        .id(u.getId())
        .name(u.getName())
        .email(u.getEmail())
        .address(u.getAddress())
        .createdAt(u.getCreatedAt())
        .orders(orders)
        .build();
  }

  @Test
  @DisplayName("возвращает список пользователей в summary")
  void getAll_ShouldReturnSummaryList() {
    User u1 = user(UUID.randomUUID(), "Ivan", "ivan@mail.com", "Addr1", List.of());
    User u2 = user(UUID.randomUUID(), "Petr", "petr@mail.com", "Addr2", List.of());

    UserSummaryResponse r1 = summaryFrom(u1);
    UserSummaryResponse r2 = summaryFrom(u2);

    when(userRepository.findAll()).thenReturn(List.of(u1, u2));
    when(userMapper.toSummary(u1)).thenReturn(r1);
    when(userMapper.toSummary(u2)).thenReturn(r2);

    List<UserSummaryResponse> result = userService.getAll();

    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(r1, result.get(0));
    assertEquals(r2, result.get(1));

    verify(userRepository, times(1)).findAll();
    verify(userMapper, times(1)).toSummary(u1);
    verify(userMapper, times(1)).toSummary(u2);
    verifyNoInteractions(orderRepository);
  }

  @Test
  @DisplayName("пользователь найден -> подгружаем заказы с items и возвращаем details")
  void getByIdWithOrders_WhenUserExists_ShouldReturnDetailsWithOrders() {
    UUID id = userId();

    User baseUser = user(id, "Ivan", "ivan@mail.com", "Addr", List.of());

    Order o1 = Order.builder().id(UUID.randomUUID()).build();
    Order o2 = Order.builder().id(UUID.randomUUID()).build();
    List<Order> ordersWithItems = List.of(o1, o2);

    List<OrderDetailsResponse> orderDtos = List.of(mock(OrderDetailsResponse.class), mock(OrderDetailsResponse.class));
    UserDetailsResponse expected = detailsFrom(baseUser, orderDtos);

    when(userRepository.findUserWithOrdersById(id)).thenReturn(Optional.of(baseUser));
    when(orderRepository.findAllByUserId(id)).thenReturn(ordersWithItems);
    when(userMapper.toDetails(baseUser)).thenReturn(expected);

    UserDetailsResponse result = userService.getByIdWithOrders(id);

    assertNotNull(result);
    assertEquals(expected, result);

    assertEquals(ordersWithItems, baseUser.getOrders());

    verify(userRepository, times(1)).findUserWithOrdersById(id);
    verify(orderRepository, times(1)).findAllByUserId(id);
    verify(userMapper, times(1)).toDetails(baseUser);
  }

  @Test
  @DisplayName("пользователь не найден -> NotFoundException")
  void getByIdWithOrders_WhenUserNotFound_ShouldThrow() {
    UUID id = userId();
    when(userRepository.findUserWithOrdersById(id)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> userService.getByIdWithOrders(id));

    verify(userRepository, times(1)).findUserWithOrdersById(id);
    verifyNoInteractions(orderRepository);
    verifyNoInteractions(userMapper);
  }

  @Test
  @DisplayName("успешное создание нового пользователя")
  void create_WhenEmailNotExists_ShouldSaveAndReturnSummary() {
    CreateUserRequest dto = new CreateUserRequest("Ivan", "ivan@mail.com", "Addr");

    User saved = user(UUID.randomUUID(), dto.name(), dto.email(), dto.address(), List.of());
    UserSummaryResponse expected = summaryFrom(saved);

    when(userRepository.existsByEmail(dto.email())).thenReturn(false);
    when(userRepository.save(any(User.class))).thenReturn(saved);
    when(userMapper.toSummary(saved)).thenReturn(expected);

    UserSummaryResponse result = userService.create(dto);

    assertNotNull(result);
    assertEquals(expected, result);

    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(captor.capture());
    User toSave = captor.getValue();

    assertEquals(dto.name(), toSave.getName());
    assertEquals(dto.email(), toSave.getEmail());
    assertEquals(dto.address(), toSave.getAddress());

    verify(userRepository, times(1)).existsByEmail(dto.email());
    verify(userMapper, times(1)).toSummary(saved);
  }

  @Test
  @DisplayName("email уже существует -> ConflictException, save не вызывается")
  void create_WhenEmailExists_ShouldThrowConflict() {
    CreateUserRequest dto = new CreateUserRequest("Ivan", "dup@mail.com", "Addr");
    when(userRepository.existsByEmail(dto.email())).thenReturn(true);

    assertThrows(ConflictException.class, () -> userService.create(dto));

    verify(userRepository, times(1)).existsByEmail(dto.email());
    verify(userRepository, never()).save(any());
    verifyNoInteractions(userMapper);
  }

  @Test
  @DisplayName("пользователь найден, обновляем name/address, email не трогаем -> save -> summary")
  void update_WhenUserExists_UpdateNameAndAddress_ShouldSave() {
    UUID id = userId();
    User existing = user(id, "Old", "old@mail.com", "OldAddr", List.of());

    UpdateUserRequest dto = new UpdateUserRequest("NewName", null, "NewAddr");

    User saved = user(id, "NewName", "old@mail.com", "NewAddr", List.of());
    UserSummaryResponse expected = summaryFrom(saved);

    when(userRepository.findById(id)).thenReturn(Optional.of(existing));
    when(userRepository.save(existing)).thenReturn(saved);
    when(userMapper.toSummary(saved)).thenReturn(expected);

    UserSummaryResponse result = userService.update(id, dto);

    assertNotNull(result);
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
  @DisplayName("меняем email на новый, который уже занят -> ConflictException, save не вызывается")
  void update_WhenEmailChangedAndAlreadyExists_ShouldThrowConflict() {
    UUID id = userId();
    User existing = user(id, "Name", "old@mail.com", "Addr", List.of());

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
  @DisplayName("меняем email на новый свободный -> save -> summary")
  void update_WhenEmailChangedAndNotExists_ShouldSave() {
    UUID id = userId();
    User existing = user(id, "Name", "old@mail.com", "Addr", List.of());

    UpdateUserRequest dto = new UpdateUserRequest(null, "new@mail.com", null);

    User saved = user(id, "Name", "new@mail.com", "Addr", List.of());
    UserSummaryResponse expected = summaryFrom(saved);

    when(userRepository.findById(id)).thenReturn(Optional.of(existing));
    when(userRepository.existsByEmail(dto.email())).thenReturn(false);
    when(userRepository.save(existing)).thenReturn(saved);
    when(userMapper.toSummary(saved)).thenReturn(expected);

    UserSummaryResponse result = userService.update(id, dto);

    assertNotNull(result);
    assertEquals(expected, result);
    assertEquals("new@mail.com", existing.getEmail());

    verify(userRepository, times(1)).findById(id);
    verify(userRepository, times(1)).existsByEmail(dto.email());
    verify(userRepository, times(1)).save(existing);
    verify(userMapper, times(1)).toSummary(saved);
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

  @Test
  @DisplayName("пользователя нет -> NotFoundException, deleteById не вызывается")
  void delete_WhenUserNotFound_ShouldThrow() {
    UUID id = userId();
    when(userRepository.existsById(id)).thenReturn(false);

    assertThrows(NotFoundException.class, () -> userService.delete(id));

    verify(userRepository, times(1)).existsById(id);
    verify(userRepository, never()).deleteById(any());
  }
}