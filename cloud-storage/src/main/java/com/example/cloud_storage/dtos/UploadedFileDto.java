package com.example.cloud_storage.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadedFileDto {
    private String id;
    private String originalFileName;
    private String storedFileName;
    private Long size;
    private LocalDateTime uploadedAt;
}