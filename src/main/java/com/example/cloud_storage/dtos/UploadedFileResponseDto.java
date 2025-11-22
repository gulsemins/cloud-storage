package com.example.cloud_storage.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class UploadedFileResponseDto {
    private String id;
    private String originalFileName;
    private String storedFileName;
    private String contentType;
    private Long size;
    private Date createdAt;
    private LocalDateTime uploadedAt;
    private String folderId;
    private String url;
}
