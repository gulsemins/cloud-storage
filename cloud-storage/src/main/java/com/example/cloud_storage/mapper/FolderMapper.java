package com.example.cloud_storage.mapper;

import com.example.cloud_storage.dtos.CreateFolderResponseDto;
import com.example.cloud_storage.dtos.GetFolderResponseDto;
import com.example.cloud_storage.dtos.UploadedFileResponseDto;
import com.example.cloud_storage.entity.FolderEntity;
import com.example.cloud_storage.entity.UploadedFileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FolderMapper {

    //@Mapping(source = "parentFolder.id", target = "parentFolderId")
    CreateFolderResponseDto toFolderResponseDto(FolderEntity entity);


    List<GetFolderResponseDto> toFolderDtoList(List<FolderEntity> entities);


}
