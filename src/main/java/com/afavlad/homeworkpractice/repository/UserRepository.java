package com.afavlad.homeworkpractice.repository;

import com.afavlad.homeworkpractice.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

  boolean existsByEmail(String email);

  @EntityGraph(attributePaths = {"orders"})
  Optional<User> findUserWithOrdersById(UUID id);

}
