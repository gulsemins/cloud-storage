package com.example.cloud_storage.repository;

import com.example.cloud_storage.entity.PublicShareEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublicShareRepository extends JpaRepository<PublicShareEntity, String > {

}
