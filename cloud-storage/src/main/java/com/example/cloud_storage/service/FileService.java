package com.example.cloud_storage.service;

import com.example.cloud_storage.dtos.SharedFileDto;
import com.example.cloud_storage.dtos.SharedFileResponseDto;
import com.example.cloud_storage.dtos.UploadedFileDto;
import com.example.cloud_storage.dtos.UploadedFileResponseDto;
import com.example.cloud_storage.entity.SharedFileEntity;
import com.example.cloud_storage.entity.UploadedFileEntity;
import com.example.cloud_storage.entity.UserEntity;
import com.example.cloud_storage.mapper.FileMapper;
import com.example.cloud_storage.repository.FileRepository;
import com.example.cloud_storage.repository.SharedFileRepository;
import com.example.cloud_storage.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
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
    private final SharedFileRepository sharedFileRepository;
    private final FileMapper fileMapper;

    private final String bucketUrl = "https://cloudstorage-springboot-s3.s3.amazonaws.com/";

    public UploadedFileResponseDto storeFile(MultipartFile file, String userId) throws IOException {
        Path uploadDir = Paths.get("uploads");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        UserEntity user = userRepository.findById(userId).get();

        String storedFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        Path filepath = Paths.get("uploads", storedFileName);
        Files.copy(file.getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);

        // === 2. S3’e de kaydet (yeni eklenen kısım) ===
        try {
            String bucketUrl = "https://cloudstorage-springboot-s3.s3.amazonaws.com/" + storedFileName;

            RestTemplate restTemplate = new RestTemplate();
            RequestEntity<byte[]> request = RequestEntity
                    .put(new URI(bucketUrl))
                    .header(HttpHeaders.CONTENT_TYPE, file.getContentType())
                    .body(file.getBytes());

            ResponseEntity<String> response = restTemplate.exchange(request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("S3 upload successful: " + bucketUrl);
            } else {
                System.out.println("S3 upload failed: " + response.getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("S3 upload error: " + e.getMessage());
        }


        UploadedFileEntity uploadedFile = new UploadedFileEntity();
        uploadedFile.setOriginalFileName(file.getOriginalFilename());
        uploadedFile.setStoredFileName(storedFileName);
        uploadedFile.setSize(file.getSize());
        uploadedFile.setUser(user);

        UploadedFileEntity saved = fileRepository.save(uploadedFile);
        return fileMapper.toUploadedFileResponseDto(saved);
    }

    public List<UploadedFileResponseDto> listFilesByUser(String userId) {

        List<UploadedFileEntity> files = fileRepository.findByUserId(userId);
        return fileMapper.toUploadedFileResponseDtoList(files);
    }

    public ResponseEntity<Resource> downloadFile(String fileId, UserEntity user) throws IOException {

        UploadedFileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        String userId = user.getId();
        if (!file.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }

        Path filePath = Paths.get("uploads", file.getStoredFileName());
        if (!Files.exists(filePath)) {
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

    public ResponseEntity<SharedFileResponseDto> shareFile(SharedFileDto sharedFileDto, String userId) throws IOException {
        UserEntity sharedBy = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        UserEntity sharedWith = userRepository.findByUsername(sharedFileDto.getSharedWithUsername());
        if (sharedWith == null) {
            throw new UsernameNotFoundException("Shared-with user not found");
        }
        UploadedFileEntity sharedFile = fileRepository.findById(sharedFileDto.getFileId()).orElseThrow(() -> new RuntimeException("File not found"));

        SharedFileEntity saveSharedFile = new SharedFileEntity();
        saveSharedFile.setSharedWith(sharedWith);
        saveSharedFile.setSharedBy(sharedBy);
        saveSharedFile.setFile(sharedFile);

        SharedFileEntity saved = sharedFileRepository.save(saveSharedFile);

        return ResponseEntity.ok(fileMapper.tosharedfileResponseDto(saved));
    }

    public List<SharedFileResponseDto> getSharedWithFiles(String userId) throws IOException {

        return fileMapper.toSharedFileResponseDtoList(sharedFileRepository.findBySharedWithId(userId));
    }
}
