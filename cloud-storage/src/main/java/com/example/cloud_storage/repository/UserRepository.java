package com.example.cloud_storage.repository;

import com.example.cloud_storage.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    UserEntity findByEmail(String email);
    UserEntity findByUsername(String username);
    Optional<UserEntity> findById(String id);
    boolean existsByUsername(String username);

}
