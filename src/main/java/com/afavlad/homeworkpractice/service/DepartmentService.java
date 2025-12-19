package com.afavlad.homeworkpractice.service;

import com.afavlad.homeworkpractice.dto.request.DepartmentRequest;
import com.afavlad.homeworkpractice.dto.response.DepartmentResponse;
import com.afavlad.homeworkpractice.entity.Department;
import com.afavlad.homeworkpractice.exception.ConflictException;
import com.afavlad.homeworkpractice.exception.NotFoundException;
import com.afavlad.homeworkpractice.mapper.DepartmentMapper;
import com.afavlad.homeworkpractice.repository.DepartmentRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DepartmentService {

  private final DepartmentRepository departmentRepository;
  private final DepartmentMapper departmentMapper;

  @Transactional
  public DepartmentResponse create(DepartmentRequest request) {
    if (departmentRepository.existsByName(request.name())) {
      throw new ConflictException("Department with name '" + request.name() + "' already exists");
    }
    Department saved = departmentRepository.save(departmentMapper.toEntity(request));
    return departmentMapper.toResponse(saved);
  }

  @Transactional(readOnly = true)
  public DepartmentResponse getById(UUID id) {
    Department department = departmentRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Department with id " + id + " not found"));
    return departmentMapper.toResponse(department);
  }

  @Transactional(readOnly = true)
  public List<DepartmentResponse> getAll() {
    return departmentRepository.findAll().stream()
        .map(departmentMapper::toResponse)
        .toList();
  }

  @Transactional
  public DepartmentResponse update(UUID id, DepartmentRequest request) {
    Department department = departmentRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Department with id " + id + " not found"));

    String newName = request.name();
    if (!department.getName().equalsIgnoreCase(newName) && departmentRepository.existsByName(
        newName)) {
      throw new ConflictException("Department with name '" + newName + "' already exists");
    }

    departmentMapper.updateEntity(request, department);
    Department saved = departmentRepository.save(department);
    return departmentMapper.toResponse(saved);
  }

  @Transactional
  public void delete(UUID id) {
    if (!departmentRepository.existsById(id)) {
      throw new NotFoundException("Department with id " + id + " not found");
    }
    departmentRepository.deleteById(id);
  }
}
