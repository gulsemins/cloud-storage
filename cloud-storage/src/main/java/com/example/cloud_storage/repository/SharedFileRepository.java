package com.example.cloud_storage.repository;

import com.example.cloud_storage.entity.SharedFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SharedFileRepository extends JpaRepository <SharedFileEntity, String >{
}
