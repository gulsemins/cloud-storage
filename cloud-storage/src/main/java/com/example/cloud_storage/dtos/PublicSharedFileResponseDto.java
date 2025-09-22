package com.example.cloud_storage.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class PublicSharedFileResponseDto {
    private String id;
    private UploadedFileResponseDto file;
    private UserResponseDto createdBy;
    private LocalDateTime expiresAt;
}
