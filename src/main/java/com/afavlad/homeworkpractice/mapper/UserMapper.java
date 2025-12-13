package com.afavlad.homeworkpractice.mapper;

import com.afavlad.homeworkpractice.dto.response.UserDetailsResponse;
import com.afavlad.homeworkpractice.dto.response.UserSummaryResponse;
import com.afavlad.homeworkpractice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OrderMapper.class})
public interface UserMapper {

  @Mapping(target = "createdAt", source = "createdAt")
  UserSummaryResponse toSummary(User user);

  @Mapping(target = "createdAt", source = "createdAt")
  UserDetailsResponse toDetails(User user);
}
