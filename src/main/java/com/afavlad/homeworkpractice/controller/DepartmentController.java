package com.afavlad.homeworkpractice.controller;

import com.afavlad.homeworkpractice.dto.request.DepartmentRequest;
import com.afavlad.homeworkpractice.dto.response.DepartmentResponse;
import com.afavlad.homeworkpractice.service.DepartmentService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/departments")
public class DepartmentController {

  private final DepartmentService departmentService;

  @GetMapping("/{id}")
  public ResponseEntity<DepartmentResponse> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(departmentService.getById(id));
  }

  @GetMapping
  public ResponseEntity<List<DepartmentResponse>> getAll() {
    return ResponseEntity.ok(departmentService.getAll());
  }

  @PostMapping
  public ResponseEntity<DepartmentResponse> create(@Valid @RequestBody DepartmentRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .contentType(MediaType.APPLICATION_JSON)
        .body(departmentService.create(request));
  }

  @PutMapping("/{id}")
  public ResponseEntity<DepartmentResponse> update(@PathVariable UUID id,
      @Valid @RequestBody DepartmentRequest request) {
    return ResponseEntity.ok(departmentService.update(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    departmentService.delete(id);
    return ResponseEntity.noContent().build();
  }
}