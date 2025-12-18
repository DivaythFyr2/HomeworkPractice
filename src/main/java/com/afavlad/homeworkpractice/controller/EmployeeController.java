package com.afavlad.homeworkpractice.controller;

import com.afavlad.homeworkpractice.dto.request.EmployeeRequest;
import com.afavlad.homeworkpractice.dto.response.EmployeeResponse;
import com.afavlad.homeworkpractice.dto.response.EmployeeSummaryResponse;
import com.afavlad.homeworkpractice.service.EmployeeService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/employees")
public class EmployeeController {

  private final EmployeeService employeeService;

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public EmployeeResponse getById(@PathVariable UUID id) {
    return employeeService.getById(id);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<EmployeeResponse> getAll() {
    return employeeService.getAll();
  }

  @GetMapping("/summary")
  @ResponseStatus(HttpStatus.OK)
  public List<EmployeeSummaryResponse> getSummary() {
    return employeeService.getSummary();
  }

  @GetMapping("/summary/by-department/{departmentId}")
  @ResponseStatus(HttpStatus.OK)
  public List<EmployeeSummaryResponse> getSummaryByDepartment(@PathVariable UUID departmentId) {
    return employeeService.getSummaryByDepartment(departmentId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public EmployeeResponse create(@Valid @RequestBody EmployeeRequest request) {
    return employeeService.create(request);
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public EmployeeResponse update(@PathVariable UUID id,
      @Valid @RequestBody EmployeeRequest request) {
    return employeeService.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) {
    employeeService.delete(id);
  }
}
