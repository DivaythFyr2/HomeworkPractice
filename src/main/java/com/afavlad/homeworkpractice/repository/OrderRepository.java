package com.afavlad.homeworkpractice.repository;

import com.afavlad.homeworkpractice.entity.Order;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, UUID> {

  List<Order> findAllByUserId(UUID userId);

  @EntityGraph(attributePaths = {"items"})
  Optional<Order> findWithItemsById(UUID id);

}
