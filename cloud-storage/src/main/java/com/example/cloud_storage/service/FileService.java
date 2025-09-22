package com.example.cloud_storage.service;

import com.example.cloud_storage.dtos.PublicSharedFileResponseDto;
import com.example.cloud_storage.dtos.SharedFileDto;
import com.example.cloud_storage.dtos.SharedFileResponseDto;
import com.example.cloud_storage.dtos.UploadedFileResponseDto;
import com.example.cloud_storage.entity.*;
import com.example.cloud_storage.mapper.FileMapper;
import com.example.cloud_storage.mapper.PublicShareMapper;
import com.example.cloud_storage.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.AccessDeniedException;
import java.time.Duration;
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
    private final S3Service s3Service;
    private final FolderRepository folderRepository; // Klasör bulmak için hala gerekli
    private final FolderService folderService;
    private final PublicShareRepository publicShareRepository;
    private final PublicShareMapper publicShareMapper;
    public UploadedFileResponseDto storeFile(MultipartFile file, String userId, String folderId) throws IOException {
     //   Path uploadDir = Paths.get("uploads");
      //  if (!Files.exists(uploadDir)) {
      //      Files.createDirectories(uploadDir);
      //  }
        UserEntity user = userRepository.findById(userId).get();

        FolderEntity parentFolder = null;
        String s3PathPrefix = user.getId() + "/";

        if (folderId != null && !folderId.isEmpty()) {
            parentFolder = folderRepository.findById(folderId)
                    .orElseThrow(() -> new EntityNotFoundException("Folder not found"));
            if (!parentFolder.getUser().getId().equals(userId)) {
                throw new SecurityException("Access denied to this folder");
            }
            s3PathPrefix = folderService.buildS3PathForFolder(parentFolder);
        }

        String storedFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        String fullS3Key = s3PathPrefix + storedFileName;

       // s3Service.uploadFile(file, storedFileName);
        s3Service.uploadFile(file, fullS3Key);


      //  Path filepath = Paths.get("uploads", storedFileName);
     //   Files.copy(file.getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);


        UploadedFileEntity uploadedFile = new UploadedFileEntity();
        uploadedFile.setOriginalFileName(file.getOriginalFilename());
        uploadedFile.setStoredFileName(fullS3Key);
        //uploadedFile.setStoredFileName(storedFileName);
        uploadedFile.setSize(file.getSize());
        uploadedFile.setUser(user);
        uploadedFile.setContentType(file.getContentType());
        uploadedFile.setFolder(parentFolder);

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

        boolean isOwner = file.getUser().getId().equals(userId);

        boolean isSharedWith = sharedFileRepository.existsByFileIdAndSharedWithId(fileId, userId);


        if (!isOwner && !isSharedWith) {
            throw new RuntimeException("Unauthorized access");
        }

        //  S3'ten pre-signed URL üret
        URL presignedUrl = s3Service.generatePresignedUrl(file.getStoredFileName(), Duration.ofMinutes(30));

        //  Resource oluştur
        UrlResource resource = new UrlResource(presignedUrl);



        //Path filePath = Paths.get("uploads", file.getStoredFileName());
        //if (!Files.exists(filePath)) {
        //  throw new RuntimeException("File not found on disk");
        //}

        //Resource resource = new UrlResource(filePath.toUri());
        //String contentType = Files.probeContentType(Paths.get(file.getOriginalFileName()));

        String contentType = file.getContentType();


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

    public ResponseEntity<String> createPublicShareLink(String fileId, String userId) throws  IOException{

        UploadedFileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File could not found."));

        if (!file.getUser().getId().equals(userId)){
            throw new AccessDeniedException("You can not access this file!");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() ->  new UsernameNotFoundException("User could not found."));

        PublicShareEntity publicShareEntity = new PublicShareEntity();
        publicShareEntity.setFile(file);
        publicShareEntity.setCreatedBy(user);
        publicShareEntity.setExpiresAt(LocalDateTime.now().plusDays(1));

        PublicShareEntity savedEntity = publicShareRepository.save(publicShareEntity);
        PublicSharedFileResponseDto responseDto = publicShareMapper.toPublicSharedDto(savedEntity);

        String baseUrl = "http://localhost:8080";

        String link = baseUrl + "/" + savedEntity.getId() + "/publicDownload"  ;
        return ResponseEntity.status(HttpStatus.CREATED).body(link);
    }
    public ResponseEntity<Resource> downloadPublicSharedFile(String publicShareFileId){

        PublicShareEntity publicShareEntity = publicShareRepository.findById(publicShareFileId)
                .orElseThrow(()-> new RuntimeException("Public Share File not found"));

        UploadedFileEntity file = publicShareEntity.getFile();

        //  S3'ten pre-signed URL üret
        URL presignedUrl = s3Service.generatePresignedUrl(file.getStoredFileName(), Duration.ofMinutes(30));

        //  Resource oluştur
        UrlResource resource = new UrlResource(presignedUrl);


        String contentType = file.getContentType();


        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType)) //dosya tipine göre dinamik görüntülemek için:
                .header(HttpHeaders.CONTENT_DISPOSITION, //browsera bu dosyayla ne yapılack diye haber verir
                        "attachment; filename=\"" + file.getOriginalFileName() + "\"") //attachment = "Bu dosyayı download et" inline = "Bu dosyayı browser'da göster" (alternatif)
                .body(resource);
    }
}
