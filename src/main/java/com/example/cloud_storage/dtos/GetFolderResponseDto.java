package com.example.cloud_storage.dtos;

import com.example.cloud_storage.entity.UserEntity;
import lombok.Data;

import java.util.Date;

@Data
public class GetFolderResponseDto {
    private String id;
    private String name;
    private UserResponseDto user;
    private Date createdAt;
    private String parentFolderId;
}
