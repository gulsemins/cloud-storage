package com.example.cloud_storage.service;

import com.example.cloud_storage.dtos.CreateFolderRequestDto;
import com.example.cloud_storage.dtos.CreateFolderResponseDto;
import com.example.cloud_storage.dtos.GetFolderResponseDto;
import com.example.cloud_storage.entity.FolderEntity;
import com.example.cloud_storage.entity.UserEntity;
import com.example.cloud_storage.mapper.FolderMapper;
import com.example.cloud_storage.repository.FolderRepository;
import com.example.cloud_storage.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;
    private final UserRepository userRepository;
    private  final S3Service s3Service;
    private final FolderMapper folderMapper;

    public CreateFolderResponseDto createFolder(CreateFolderRequestDto request, String userId) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        FolderEntity parentFolder = null;
        if (request.getParentFolderId() != null && !request.getParentFolderId().isEmpty()) {
            parentFolder = folderRepository.findById(request.getParentFolderId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent folder not found"));

            if (!parentFolder.getUser().getId().equals(userId)) {
                throw new SecurityException("Access denied to create subfolder here");
            }
        }
        FolderEntity newFolder = new FolderEntity();
        newFolder.setName(request.getName());
        newFolder.setUser(user);
        newFolder.setParentFolder(parentFolder);

        FolderEntity savedFolder = folderRepository.save(newFolder);

        String s3FolderKey = buildS3PathForFolder(savedFolder);
        s3Service.createFolder(s3FolderKey);
        return folderMapper.toFolderResponseDto(savedFolder);
    }

    public List<GetFolderResponseDto> listRootFoldersByUser(String userId){

        List<FolderEntity> parentFolders = folderRepository.findByUserIdAndParentFolderIsNull(userId);
        return folderMapper.toFolderDtoList(parentFolders);
    }

    public List<GetFolderResponseDto> listSubFolders(String id){
        List<FolderEntity> subFolders = folderRepository.findByParentFolderId(id);

        return folderMapper.toFolderDtoList(subFolders);
    }

    public String buildS3PathForFolder(FolderEntity folder) {
        if (folder == null) {
            return ""; // Hata veya ana dizin durumu
        }

        StringBuilder path = new StringBuilder();
        path.append(folder.getName()).append("/");

        FolderEntity current = folder.getParentFolder();
        while (current != null) {
            path.insert(0, current.getName() + "/");
            current = current.getParentFolder();
        }

        // En başa kullanıcı ID'sini ekle
        path.insert(0, folder.getUser().getId() + "/");

        return path.toString();
    }

    public ResponseEntity<Void> deleteFolder(String folderId, String userId) throws IOException {
        FolderEntity folderToDelete = folderRepository.findById(folderId)
                .orElseThrow(() -> new EntityNotFoundException("Folder not found with id: " + folderId));

        if (!folderToDelete.getUser().getId().equals(userId)){
            throw new AccessDeniedException("You do not have permission to delete this folder.");}

        String folderKey = folderToDelete.getUser().getId() + "/" + folderToDelete.getName() + "/";
        s3Service.deleteFolder(folderKey);


        folderRepository.delete(folderToDelete);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);    }

}