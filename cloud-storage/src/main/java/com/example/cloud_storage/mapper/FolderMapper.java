package com.example.cloud_storage.mapper;

import com.example.cloud_storage.dtos.CreateFolderResponseDto;
import com.example.cloud_storage.entity.FolderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FolderMapper {

    @Mapping(source = "parentFolder.id", target = "parentFolderId")
    CreateFolderResponseDto toFolderResponseDto(FolderEntity entity);

}
