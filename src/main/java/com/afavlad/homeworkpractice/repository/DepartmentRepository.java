package com.afavlad.homeworkpractice.repository;

import com.afavlad.homeworkpractice.entity.Department;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, UUID> {

  boolean existsByName(String name);

}
