package com.example.cloud_storage.mapper;

import com.example.cloud_storage.dtos.SharedFileDto;
import com.example.cloud_storage.dtos.SharedFileResponseDto;
import com.example.cloud_storage.dtos.UploadedFileDto;
import com.example.cloud_storage.dtos.UploadedFileResponseDto;
import com.example.cloud_storage.entity.SharedFileEntity;
import com.example.cloud_storage.entity.UploadedFileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FileMapper {

    UploadedFileResponseDto toUploadedFileResponseDto (UploadedFileEntity entity);

    List<UploadedFileResponseDto> toUploadedFileResponseDtoList(List<UploadedFileEntity> entities);

   // @Mapping(target = "fileId", source = "file.id")
  //  @Mapping(target = "sharedWithUsername", source = "sharedWith.username")
   // SharedFileDto toSharedFileDto(SharedFileEntity entity);

   // @Mapping(target = "fileId", source = "file.id")
   // @Mapping(target = "sharedWithUsername", source = "sharedWith.username")
    //List<SharedFileDto> toSharedFileDtoList(List<SharedFileEntity> entities);


    SharedFileResponseDto tosharedfileResponseDto(SharedFileEntity entity);
    List<SharedFileResponseDto> toSharedFileResponseDtoList(List<SharedFileEntity> entities);
}


