package com.example.cloud_storage.entity;

import com.example.cloud_storage.dtos.UserResponseDto;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Data
public class PublicShareEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @ManyToOne
    @JoinColumn(name = "file_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UploadedFileEntity file;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private UserEntity createdBy;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

}
