package com.example.cloud_storage.mapper;

import com.example.cloud_storage.dtos.CreateFolderResponseDto;
import com.example.cloud_storage.dtos.GetFolderResponseDto;
import com.example.cloud_storage.entity.FolderEntity;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface FolderMapper {

    //@Mapping(source = "parentFolder.id", target = "parentFolderId")
    CreateFolderResponseDto toFolderResponseDto(FolderEntity entity);


    List<GetFolderResponseDto> toFolderDtoList(List<FolderEntity> entities);


}
