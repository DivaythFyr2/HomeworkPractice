package com.afavlad.homeworkpractice.repository;

import com.afavlad.homeworkpractice.entity.Order;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, UUID> {

  Page<Order> findAllByUserId(UUID userId, Pageable pageable);

  @EntityGraph(attributePaths = {"items"})
  Optional<Order> findWithItemsById(UUID id);

}
