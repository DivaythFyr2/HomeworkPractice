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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/employees")
public class EmployeeController {

  private final EmployeeService employeeService;

  @GetMapping("/{id}")
  public ResponseEntity<EmployeeResponse> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(employeeService.getById(id));
  }

  @GetMapping
  public ResponseEntity<List<EmployeeResponse>> getAll() {
    return ResponseEntity.ok(employeeService.getAll());
  }

  @GetMapping("/summary")
  public ResponseEntity<List<EmployeeSummaryResponse>> getSummary() {
    return ResponseEntity.ok(employeeService.getSummary());
  }

  @GetMapping("/summary/by-department/{departmentId}")
  public ResponseEntity<List<EmployeeSummaryResponse>> getSummaryByDepartment(
      @PathVariable UUID departmentId) {
    return ResponseEntity.ok(employeeService.getSummaryByDepartment(departmentId));
  }

  @PostMapping
  public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody EmployeeRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .contentType(MediaType.APPLICATION_JSON)
        .body(employeeService.create(request));
  }

  @PutMapping("/{id}")
  public ResponseEntity<EmployeeResponse> update(@PathVariable UUID id,
      @Valid @RequestBody EmployeeRequest request) {
    return ResponseEntity.ok(employeeService.update(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    employeeService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
