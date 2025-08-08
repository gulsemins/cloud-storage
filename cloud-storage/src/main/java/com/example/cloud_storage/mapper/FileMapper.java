package com.example.cloud_storage.mapper;

import com.example.cloud_storage.dtos.SharedFileDto;
import com.example.cloud_storage.dtos.UploadedFileDto;
import com.example.cloud_storage.entity.SharedFileEntity;
import com.example.cloud_storage.entity.UploadedFileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FileMapper {

    UploadedFileDto toUploadedFileDto(UploadedFileEntity entity);

    List<UploadedFileDto> toDtoList(List<UploadedFileEntity> entities);

    @Mapping(target = "fileId", source = "file.id")
    @Mapping(target = "sharedWithUsername", source = "sharedWith.username")
    SharedFileDto toSharedFileDto(SharedFileEntity entity);
}
