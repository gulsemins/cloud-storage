package com.example.cloud_storage.service;

import com.example.cloud_storage.dtos.*;
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

    public UploadedFileResponseDto uploadFileWithPresign(UploadFileRequestDto uploadFileRequestDto, UserEntity user, String folderId){
        FolderEntity parentFolder = null;
        String s3PathPrefix = user.getId() + "/";

        if (folderId != null && !folderId.isEmpty()) {
            parentFolder = folderRepository.findById(folderId)
                    .orElseThrow(() -> new EntityNotFoundException("Folder not found"));
            if (!parentFolder.getUser().getId().equals(user.getId())) {
                throw new SecurityException("Access denied to this folder");
            }
            s3PathPrefix = folderService.buildS3PathForFolder(parentFolder);
        }

        String storedFileName = UUID.randomUUID() + "_" + uploadFileRequestDto.getOriginalFileName();

        String fullS3Key = s3PathPrefix + storedFileName;

        UploadedFileEntity uploadedFile = new UploadedFileEntity();
        uploadedFile.setOriginalFileName(uploadFileRequestDto.getOriginalFileName());
        uploadedFile.setStoredFileName(fullS3Key);
        uploadedFile.setContentType(uploadFileRequestDto.getContentType());
        uploadedFile.setSize(uploadFileRequestDto.getSize());
        uploadedFile.setUser(user);

        uploadedFile.setFolder(parentFolder);

        URL presignURL = s3Service.uploadWithPresignedUrl(fullS3Key, uploadFileRequestDto.getContentType(), Duration.ofMinutes(30));

        UploadedFileEntity saved = fileRepository.save(uploadedFile);

        UploadedFileResponseDto uploadedFileResponseDto = fileMapper.toUploadedFileResponseDto(saved);
        uploadedFileResponseDto.setUrl(presignURL.toString());

        return uploadedFileResponseDto;
    }

    public List<UploadedFileResponseDto> listFilesByUser(String userId) {

        List<UploadedFileEntity> files = fileRepository.findByUserId(userId);
        return fileMapper.toUploadedFileResponseDtoList(files);
    }

    public ResponseEntity<URL> downloadFile(String fileId, UserEntity user) throws IOException {

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



        //Path filePath = Paths.get("uploads", file.getStoredFileName());
        //if (!Files.exists(filePath)) {
        //  throw new RuntimeException("File not found on disk");
        //}

        //Resource resource = new UrlResource(filePath.toUri());
        //String contentType = Files.probeContentType(Paths.get(file.getOriginalFileName()));

        String contentType = file.getContentType();


        return ResponseEntity.ok(presignedUrl);
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

    public ResponseEntity<String> createPublicShareLink(String fileId, String userId, int expirationHours) throws  IOException{

        UploadedFileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File could not found."));

        if (!file.getUser().getId().equals(userId)){
            throw new AccessDeniedException("You can not access this file!");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() ->  new UsernameNotFoundException("User could not found."));

        // Validate expiration hours (between 1 hour and 30 days)
        if (expirationHours < 1 || expirationHours > 720) {
            throw new IllegalArgumentException("Expiration hours must be between 1 and 720 (30 days)");
        }

        PublicShareEntity publicShareEntity = new PublicShareEntity();
        publicShareEntity.setFile(file);
        publicShareEntity.setCreatedBy(user);
        publicShareEntity.setExpiresAt(LocalDateTime.now().plusHours(expirationHours));

        PublicShareEntity savedEntity = publicShareRepository.save(publicShareEntity);
        PublicSharedFileResponseDto responseDto = publicShareMapper.toPublicSharedDto(savedEntity);

        String baseUrl = "http://localhost:5173";

        String link = baseUrl + "/" + savedEntity.getId() + "/publicDownload"  ;
        return ResponseEntity.status(HttpStatus.CREATED).body(link);

    }
    public ResponseEntity<URL> downloadPublicSharedFile(String publicShareFileId){

        PublicShareEntity publicShareEntity = publicShareRepository.findById(publicShareFileId)
                .orElseThrow(()-> new RuntimeException("Public Share File not found"));

        // Check if the link has expired
        if (publicShareEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("This public link has expired");
        }

        UploadedFileEntity file = publicShareEntity.getFile();

        //  S3'ten pre-signed URL üret
        URL presignedUrl = s3Service.generatePresignedUrl(file.getStoredFileName(), Duration.ofMinutes(30));


        return ResponseEntity.ok(presignedUrl);
    }

    public ResponseEntity<Void> deleteFile(String fileId, String userId) throws IOException{
        UploadedFileEntity fileToDelete = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found with id: " + fileId));

        if(!(fileToDelete.getUser().getId().equals(userId)) ){
            throw new AccessDeniedException("You do not have permission to delete this file.");
        }

        s3Service.deleteFile(fileToDelete.getStoredFileName());
        fileRepository.delete(fileToDelete);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);    }

    public ResponseEntity<Void> deleteFolder(String folderId, String userId) throws IOException{
        FolderEntity folderToDelete = folderRepository.findById(folderId)
                .orElseThrow(() -> new EntityNotFoundException("Folder not found with id: " + folderId));

        if (!folderToDelete.getUser().getId().equals(userId)){
            throw new AccessDeniedException("You do not have permission to delete this folder.");}

        String folderKey = folderToDelete.getUser().getId() + "/" + folderToDelete.getName() + "/";
        s3Service.deleteFolder(folderKey);


        folderRepository.delete(folderToDelete);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);    }

}

