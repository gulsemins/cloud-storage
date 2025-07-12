package com.example.cloud_storage.repository;

import com.example.cloud_storage.entity.UploadedFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<UploadedFileEntity, String> {

    List<UploadedFileEntity> findByUserId(String id);
}
