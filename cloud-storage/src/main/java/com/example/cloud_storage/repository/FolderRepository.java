package com.example.cloud_storage.repository;

import com.example.cloud_storage.dtos.CreateFolderResponseDto;
import com.example.cloud_storage.entity.FolderEntity;
import com.example.cloud_storage.entity.UploadedFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<FolderEntity, String> {
    List<FolderEntity> findByUserId(String id);
    List<FolderEntity> findByParentFolderId(String id);
    List<FolderEntity> findByUserIdAndParentFolderIsNull(String id);



}
