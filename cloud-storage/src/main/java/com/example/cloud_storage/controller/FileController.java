package com.example.cloud_storage.controller;

import com.example.cloud_storage.CustomUserDetails;
import com.example.cloud_storage.dtos.SharedFileDto;
import com.example.cloud_storage.dtos.SharedFileResponseDto;
import com.example.cloud_storage.dtos.UploadedFileResponseDto;
import com.example.cloud_storage.entity.UserEntity;
import com.example.cloud_storage.repository.FileRepository;
import com.example.cloud_storage.service.FileService;
import com.example.cloud_storage.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@AllArgsConstructor
@RestController
public class FileController {

    private final FileService fileService;
    private final UserService userService;
    private final FileRepository fileRepository;

    @PostMapping("/upload")
    public ResponseEntity<UploadedFileResponseDto> uploadFile(@RequestParam("file") MultipartFile file,
                                                              @AuthenticationPrincipal CustomUserDetails  userDetails) throws IOException {
        String username = userDetails.getUsername();
        UserEntity user = userService.getUserByUsername(username);
      UploadedFileResponseDto response = fileService.storeFile(file, user.getId());
        return ResponseEntity.ok(response);

    }

    @GetMapping("/files")
    public ResponseEntity<List<UploadedFileResponseDto>> getUserFiles(@AuthenticationPrincipal CustomUserDetails  userDetails){
        String username = userDetails.getUsername();
        UserEntity user = userService.getUserByUsername(username);
        List<UploadedFileResponseDto> files = fileService.listFilesByUser(user.getId());
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
    public ResponseEntity<SharedFileResponseDto> shareFile(@RequestBody SharedFileDto sharedFile, @AuthenticationPrincipal CustomUserDetails  userDetails)throws IOException{

        String username = userDetails.getUsername();
        UserEntity user = userService.getUserByUsername(username);
        return fileService.shareFile(sharedFile, user.getId());
    }
    @GetMapping("/shared-with-me")
    public List<SharedFileResponseDto> getSharedWithMeFiles(@AuthenticationPrincipal CustomUserDetails userDetails) throws IOException{

        String username = userDetails.getUsername();
        UserEntity user = userService.getUserByUsername(username);
        return fileService.getSharedWithFiles(user.getId());
    }

}



