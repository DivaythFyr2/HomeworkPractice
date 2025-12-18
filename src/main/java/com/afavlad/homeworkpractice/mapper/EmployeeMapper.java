package com.afavlad.homeworkpractice.mapper;

import com.afavlad.homeworkpractice.dto.response.EmployeeResponse;
import com.afavlad.homeworkpractice.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

  @Mapping(source = "department.name", target = "departmentName")
  EmployeeResponse toResponse(Employee employee);

}
