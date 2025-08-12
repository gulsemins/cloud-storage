package com.example.cloud_storage.controller;

import com.example.cloud_storage.CustomUserDetails;
import com.example.cloud_storage.dtos.SharedFileDto;
import com.example.cloud_storage.dtos.UploadedFileDto;
import com.example.cloud_storage.entity.UploadedFileEntity;
import com.example.cloud_storage.entity.UserEntity;
import com.example.cloud_storage.repository.FileRepository;
import com.example.cloud_storage.service.FileService;
import com.example.cloud_storage.service.UserService;
import jakarta.annotation.Resources;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
public class FileController {

    private final FileService fileService;
    private final UserService userService;
    private final FileRepository fileRepository;

    @PostMapping("/upload")
    public ResponseEntity<UploadedFileDto> uploadFile(@RequestParam("file") MultipartFile file,
                                                         @AuthenticationPrincipal CustomUserDetails  userDetails) throws IOException {
        String username = userDetails.getUsername();
        UserEntity user = userService.getUserByUsername(username);
      UploadedFileDto response = fileService.storeFile(file, user.getId());
        return ResponseEntity.ok(response);

    }

    @GetMapping("/files")
    public ResponseEntity<List<UploadedFileDto>> getUserFiles(@AuthenticationPrincipal CustomUserDetails  userDetails){
        String username = userDetails.getUsername();
        UserEntity user = userService.getUserByUsername(username);
        List<UploadedFileDto> files = fileService.listFilesByUser(user.getId());
        return ResponseEntity.ok(files);
    }

    @GetMapping("{fileId}/download")
    public ResponseEntity<Resource> downloadFile (@PathVariable String fileId,
                                                         @AuthenticationPrincipal CustomUserDetails userDetails) throws IOException {

        String username = userDetails.getUsername();
        UserEntity user = userService.getUserByUsername(username);
        return fileService.downloadFile(fileId, user);
}
    @PostMapping("/share")
    public ResponseEntity<SharedFileDto> shareFile(@RequestBody SharedFileDto sharedFile, @AuthenticationPrincipal CustomUserDetails  userDetails)throws IOException{

        String username = userDetails.getUsername();
        UserEntity user = userService.getUserByUsername(username);
        return fileService.shareFile(sharedFile, user.getId());
    }
    @GetMapping("/shared-with-me")
    public List<SharedFileDto> getSharedWithMeFiles( @AuthenticationPrincipal CustomUserDetails userDetails) throws IOException{

        String username = userDetails.getUsername();
        UserEntity user = userService.getUserByUsername(username);
        return fileService.getSharedWithFiles(user.getId());
    }

}



