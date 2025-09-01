package com.example.cloud_storage.repository;

import com.example.cloud_storage.entity.SharedFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SharedFileRepository extends JpaRepository <SharedFileEntity, String >{
    List<SharedFileEntity> findBySharedWithId(String id);

    boolean existsByFileIdAndSharedWithId(String fileId, String sharedWithId);

}
