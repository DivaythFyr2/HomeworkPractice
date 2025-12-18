package com.afavlad.homeworkpractice.controller;

import com.afavlad.homeworkpractice.dto.request.DepartmentRequest;
import com.afavlad.homeworkpractice.dto.response.DepartmentResponse;
import com.afavlad.homeworkpractice.service.DepartmentService;
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
@RequestMapping("/api/v1/departments")
public class DepartmentController {

  private final DepartmentService departmentService;

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public DepartmentResponse getById(@PathVariable UUID id) {
    return departmentService.getById(id);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<DepartmentResponse> getAll() {
    return departmentService.getAll();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public DepartmentResponse create(@Valid @RequestBody DepartmentRequest request) {
    return departmentService.create(request);
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public DepartmentResponse update(@PathVariable UUID id,
      @Valid @RequestBody DepartmentRequest request) {
    return departmentService.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) {
    departmentService.delete(id);
  }
}