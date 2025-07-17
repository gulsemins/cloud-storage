package com.example.cloud_storage.repository;

import com.example.cloud_storage.entity.UploadedFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<UploadedFileEntity, String> {

    List<UploadedFileEntity> findByUserId(String id);
}
