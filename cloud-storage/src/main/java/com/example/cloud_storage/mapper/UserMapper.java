package com.example.cloud_storage.mapper;

import com.example.cloud_storage.dtos.RegisterRequestDto;
import com.example.cloud_storage.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
   UserEntity toEntity(RegisterRequestDto registerRequestDto);

   RegisterRequestDto toRegisterRequestDto(UserEntity user);


}
