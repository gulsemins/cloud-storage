package com.example.cloud_storage.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class UploadedFileEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String originalFileName;
    private String storedFileName;
    private String contentType;
    private Long size;

    @Column(name = "uploaded_at")
    @CreationTimestamp
    private LocalDateTime uploadedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<SharedFileEntity> sharedFiles = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="folder_id", nullable = true)
    private FolderEntity folder;

    @Column(name = "created_at")
    @CreationTimestamp
    private Date createdAt;



}