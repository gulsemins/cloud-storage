package com.example.cloud_storage.repository;

import com.example.cloud_storage.entity.UserEntity;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    UserEntity findByEmail(String email);
    UserEntity findByUsername(String username);
    UserEntity findById(String id);
}
