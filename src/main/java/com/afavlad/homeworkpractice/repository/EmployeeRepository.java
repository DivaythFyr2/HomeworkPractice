package com.afavlad.homeworkpractice.repository;

import com.afavlad.homeworkpractice.entity.Employee;
import com.afavlad.homeworkpractice.projection.EmployeeProjection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

  List<EmployeeProjection> findAllBy();

  List<EmployeeProjection> findByDepartmentId(UUID departmentId);

}
