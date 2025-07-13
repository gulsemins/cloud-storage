package com.example.cloud_storage.controller;

import com.example.cloud_storage.CustomUserDetails;
import com.example.cloud_storage.dtos.UploadedFileDto;
import com.example.cloud_storage.entity.UploadedFileEntity;
import com.example.cloud_storage.entity.UserEntity;
import com.example.cloud_storage.service.FileService;
import com.example.cloud_storage.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
public class FileController {

    private final FileService fileService;
    private final UserService userService;

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
}
