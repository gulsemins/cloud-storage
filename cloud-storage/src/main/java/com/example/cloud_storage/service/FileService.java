package com.example.cloud_storage.service;

import com.example.cloud_storage.dtos.UploadedFileDto;
import com.example.cloud_storage.entity.UploadedFileEntity;
import com.example.cloud_storage.entity.UserEntity;
import com.example.cloud_storage.mapper.FileMapper;
import com.example.cloud_storage.repository.FileRepository;
import com.example.cloud_storage.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
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

        String storedFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

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

    public ResponseEntity<Resource> downloadFile (String fileId, UserEntity user) throws IOException{

        UploadedFileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        String userId= user.getId();
        if (!file.getUser().getId().equals(userId)){
            throw new RuntimeException("Unauthorized access");
        }

        Path filePath = Paths.get("uploads", file.getStoredFileName());
        if (!Files.exists(filePath)){
            throw new RuntimeException("File not found on disk");
        }

        Resource resource = new UrlResource(filePath.toUri());
        String contentType = Files.probeContentType(Paths.get(file.getOriginalFileName()));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType)) //dosya tipine göre dinamik görüntülemek için:
                .header(HttpHeaders.CONTENT_DISPOSITION, //browsera bu dosyayla ne yapılack diye haber verir
                        "attachment; filename=\"" + file.getOriginalFileName() + "\"") //attachment = "Bu dosyayı download et" inline = "Bu dosyayı browser'da göster" (alternatif)
                .body(resource);
    }
    }
