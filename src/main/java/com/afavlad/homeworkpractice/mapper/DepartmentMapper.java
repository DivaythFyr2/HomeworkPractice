package com.afavlad.homeworkpractice.mapper;

import com.afavlad.homeworkpractice.dto.request.DepartmentRequest;
import com.afavlad.homeworkpractice.dto.response.DepartmentResponse;
import com.afavlad.homeworkpractice.entity.Department;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

  @Mapping(target = "id", ignore = true)
  Department toEntity(DepartmentRequest request);

  DepartmentResponse toResponse(Department department);

  @Mapping(target = "id", ignore = true)
  void updateEntity(DepartmentRequest request, @MappingTarget Department department);

}
