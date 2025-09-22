package com.example.cloud_storage.mapper;

import com.example.cloud_storage.dtos.PublicSharedFileResponseDto;
import com.example.cloud_storage.entity.PublicShareEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PublicShareMapper {
    PublicSharedFileResponseDto toPublicSharedDto (PublicShareEntity publicShareEntity);

}
