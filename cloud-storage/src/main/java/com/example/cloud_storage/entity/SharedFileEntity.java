package com.example.cloud_storage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
@Entity
@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class SharedFileEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @ManyToOne
    @JoinColumn(name = "file_id")
    private UploadedFileEntity file;

    @ManyToOne
    @JoinColumn(name = "shared_by_user_id")
    private UserEntity sharedBy;

    @ManyToOne
    @JoinColumn(name = "shared_with_user_id")
    private UserEntity sharedWith;

    private LocalDateTime sharedAt;

    // (İsteğe bağlı) Yetki seviyesi: sadece görüntüleme mi, düzenleme mi?
    //p brivate String permission;
}
