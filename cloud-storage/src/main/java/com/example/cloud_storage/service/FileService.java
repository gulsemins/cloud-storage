package com.example.cloud_storage.service;

import com.example.cloud_storage.dtos.UploadedFileDto;
import com.example.cloud_storage.entity.UploadedFileEntity;
import com.example.cloud_storage.entity.UserEntity;
import com.example.cloud_storage.mapper.FileMapper;
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
    private final FileMapper fileMapper;

    public UploadedFileDto storeFile(MultipartFile file, String userId)throws IOException {
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

        UploadedFileEntity saved = fileRepository.save(uploadedFile);
        return fileMapper.toUploadedFileDto(saved);
    }

    public List<UploadedFileDto> listFilesByUser(String userId) {

        List<UploadedFileEntity> files = fileRepository.findByUserId(userId);
        return fileMapper.toDtoList(files);
    }
}

