package com.example.cloud_storage.dtos;

import lombok.Data;

import java.util.Date;

@Data
public class CreateFolderResponseDto {
    private String id;
    private String name;
    private Date createdAt;
    private String parentFolderId;
}

