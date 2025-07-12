package com.example.cloud_storage.service;

import com.example.cloud_storage.entity.UploadedFileEntity;
import com.example.cloud_storage.entity.UserEntity;
import com.example.cloud_storage.repository.FileRepository;
import com.example.cloud_storage.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class FileService {


    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    public UploadedFileEntity storeFile(MultipartFile file, String userId)throws IOException {
        Path uploadDir = Paths.get("uploads");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        UserEntity user = userRepository.findById(userId).get();

        String storedFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        Path filepath = Paths.get("uploads", storedFileName);
        Files.copy(file.getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);

        UploadedFileEntity uploadedFile = new UploadedFileEntity();
        uploadedFile.setOriginalFileName(file.getOriginalFilename());
        uploadedFile.setStoredFileName(storedFileName);
        uploadedFile.setSize(file.getSize());
        uploadedFile.setUploadedAt(LocalDateTime.now());
        uploadedFile.setUser(user);

        return fileRepository.save(uploadedFile);
    }

    public List<UploadedFileEntity> listFilesByUser(String userId) {
        return fileRepository.findByUserId(userId);
    }
}

