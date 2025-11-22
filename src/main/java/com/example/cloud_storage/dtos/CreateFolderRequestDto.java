package com.example.cloud_storage.dtos;

import lombok.Data;

@Data
public class CreateFolderRequestDto {
    private String name;
    private String parentFolderId;
}
