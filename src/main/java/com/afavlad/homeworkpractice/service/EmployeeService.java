package com.afavlad.homeworkpractice.service;

import com.afavlad.homeworkpractice.dto.request.EmployeeRequest;
import com.afavlad.homeworkpractice.dto.response.EmployeeResponse;
import com.afavlad.homeworkpractice.dto.response.EmployeeSummaryResponse;
import com.afavlad.homeworkpractice.entity.Department;
import com.afavlad.homeworkpractice.entity.Employee;
import com.afavlad.homeworkpractice.exception.NotFoundException;
import com.afavlad.homeworkpractice.mapper.EmployeeMapper;
import com.afavlad.homeworkpractice.projection.EmployeeProjection;
import com.afavlad.homeworkpractice.repository.DepartmentRepository;
import com.afavlad.homeworkpractice.repository.EmployeeRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeService {

  private final EmployeeRepository employeeRepository;
  private final DepartmentRepository departmentRepository;
  private final EmployeeMapper employeeMapper;

  @Transactional
  public EmployeeResponse create(EmployeeRequest request) {
    Department department = departmentRepository.findById(request.departmentId())
        .orElseThrow(() -> new NotFoundException(
            "Department with id " + request.departmentId() + " not found"));

    Employee employee = new Employee(
        null,
        request.firstName(),
        request.lastName(),
        request.position(),
        request.salary(),
        department
    );

    Employee saved = employeeRepository.save(employee);
    return employeeMapper.toResponse(saved);
  }

  public EmployeeResponse getById(UUID id) {
    Employee employee = employeeRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Employee with id " + id + " not found"));
    return employeeMapper.toResponse(employee);
  }

  public List<EmployeeResponse> getAll() {
    return employeeRepository.findAll().stream()
        .map(employeeMapper::toResponse)
        .toList();
  }

  @Transactional
  public EmployeeResponse update(UUID id, EmployeeRequest request) {
    Employee employee = employeeRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Employee with id " + id + " not found"));

    Department department = departmentRepository.findById(request.departmentId())
        .orElseThrow(() -> new NotFoundException(
            "Department with id " + request.departmentId() + " not found"));

    employee.setFirstName(request.firstName());
    employee.setLastName(request.lastName());
    employee.setPosition(request.position());
    employee.setSalary(request.salary());
    employee.setDepartment(department);

    Employee saved = employeeRepository.save(employee);
    return employeeMapper.toResponse(saved);
  }

  @Transactional
  public void delete(UUID id) {
    if (!employeeRepository.existsById(id)) {
      throw new NotFoundException("Employee with id " + id + " not found");
    }
    employeeRepository.deleteById(id);
  }

  public List<EmployeeSummaryResponse> getSummary() {
    List<EmployeeProjection> projections = employeeRepository.findAllBy();
    return projections.stream()
        .map(p -> new EmployeeSummaryResponse(p.getFullName(), p.getPosition(),
            p.getDepartmentName()))
        .toList();
  }

  public List<EmployeeSummaryResponse> getSummaryByDepartment(UUID departmentId) {
    List<EmployeeProjection> projections = employeeRepository.findByDepartmentId(departmentId);
    return projections.stream()
        .map(p ->
            new EmployeeSummaryResponse(p.getFullName(), p.getPosition(), p.getDepartmentName()))
        .toList();
  }
}
