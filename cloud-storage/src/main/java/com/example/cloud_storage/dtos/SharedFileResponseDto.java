package com.example.cloud_storage.dtos;

import com.example.cloud_storage.entity.UploadedFileEntity;
import com.example.cloud_storage.entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Data
public class SharedFileResponseDto {

    private String id;
    private UploadedFileDto file;
    private UserEntity sharedBy;
    private UserEntity sharedWith;
    private Date createdAt;

}
