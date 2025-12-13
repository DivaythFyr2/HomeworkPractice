package com.afavlad.homeworkpractice.service;

import com.afavlad.homeworkpractice.dto.request.CreateUserRequest;
import com.afavlad.homeworkpractice.dto.request.UpdateUserRequest;
import com.afavlad.homeworkpractice.dto.response.UserDetailsResponse;
import com.afavlad.homeworkpractice.dto.response.UserSummaryResponse;
import com.afavlad.homeworkpractice.entity.Order;
import com.afavlad.homeworkpractice.entity.User;
import com.afavlad.homeworkpractice.exception.ConflictException;
import com.afavlad.homeworkpractice.exception.NotFoundException;
import com.afavlad.homeworkpractice.mapper.UserMapper;
import com.afavlad.homeworkpractice.repository.OrderRepository;
import com.afavlad.homeworkpractice.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

  private final UserRepository userRepository;
  private final OrderRepository orderRepository;
  private final UserMapper userMapper;

  public List<UserSummaryResponse> getAll() {
    return userRepository.findAll()
        .stream()
        .map(userMapper::toSummary)
        .toList();
  }

  public UserDetailsResponse getByIdWithOrders(UUID id) {
    User user = userRepository.findUserWithOrdersById(id)
        .orElseThrow(() -> new NotFoundException("User not found: " + id));

    List<Order> ordersWithItems = orderRepository.findAllByUserId(id);
    user.setOrders(ordersWithItems);

    return userMapper.toDetails(user);
  }

  @Transactional
  public UserSummaryResponse create(CreateUserRequest dto) {
    if (userRepository.existsByEmail(dto.email())) {
      throw new ConflictException("Email already exists: " + dto.email());
    }

    User user = User.builder()
        .name(dto.name())
        .email(dto.email())
        .address(dto.address())
        .build();

    User savedUser = userRepository.save(user);
    return userMapper.toSummary(savedUser);
}

  @Transactional
  public UserSummaryResponse update(UUID id, UpdateUserRequest dto) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("User not found: " + id));

    if (dto.name() != null) {
      user.setName(dto.name());
    }

    if (dto.email() != null && !dto.email().equals(user.getEmail())) {
      if (userRepository.existsByEmail(dto.email())) {
        throw new ConflictException("Email already exists: " + dto.email());
      }
      user.setEmail(dto.email());
    }

    if (dto.address() != null) {
      user.setAddress(dto.address());
    }

    User savedUser = userRepository.save(user);
    return userMapper.toSummary(savedUser);
  }

  @Transactional
  public void delete(UUID id) {
    if (!userRepository.existsById(id)) {
      throw new NotFoundException("User not found: " + id);
    }
    userRepository.deleteById(id);
  }
}
