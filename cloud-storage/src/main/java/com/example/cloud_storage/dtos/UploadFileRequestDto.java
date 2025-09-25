package com.example.cloud_storage.dtos;

import lombok.Data;

@Data
public class UploadFileRequestDto {
    private String originalFileName;
    private String contentType;
    private Long size;
}
