package com.example.cloud_storage.controller;

import com.example.cloud_storage.CustomUserDetails;
import com.example.cloud_storage.dtos.*;
import com.example.cloud_storage.entity.UserEntity;
import com.example.cloud_storage.repository.FileRepository;
import com.example.cloud_storage.service.FileService;
import com.example.cloud_storage.service.FolderService;
import com.example.cloud_storage.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/folder")

public class FolderController {

    private final FileService fileService;
    private final UserService userService;
    private final FolderService folderService;


    @PostMapping("/createFolders")
    public ResponseEntity<CreateFolderResponseDto> createFolder(
            @RequestBody CreateFolderRequestDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){

        UserEntity user = userService.getUserByUsername(userDetails.getUsername());

        CreateFolderResponseDto response = folderService.createFolder(request, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/allRootFolders")
    public ResponseEntity<List<GetFolderResponseDto>> listFoldersByUser(@AuthenticationPrincipal CustomUserDetails  userDetails){
        String username = userDetails.getUsername();
        UserEntity user = userService.getUserByUsername(username);

        List<GetFolderResponseDto> folders = folderService.listRootFoldersByUser(user.getId());
        return ResponseEntity.ok(folders);
    }
    @GetMapping("/subFolders/{id}")
    public ResponseEntity<List<GetFolderResponseDto>> getSubFolders(@PathVariable String id){
        List<GetFolderResponseDto> subFolders = folderService.listSubFolders(id);
        return ResponseEntity.ok(subFolders);
    }

    @DeleteMapping("/{folderId}")
    public ResponseEntity<Void> deleteFolder(@PathVariable String folderId,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) throws IOException {
        String username = userDetails.getUsername();
        UserEntity user = userService.getUserByUsername(username);

        return folderService.deleteFolder(folderId, user.getId());
    }

    @PutMapping("/{folderId}/changeName")
    public ResponseEntity<String> changeName(@PathVariable String folderId,
                                             @RequestBody ChangeFolderNameRequestDto request,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) throws IOException{
        String username = userDetails.getUsername();
        UserEntity user = userService.getUserByUsername(username);

        return folderService.changeFolderName(folderId, user.getId(), request.getName());
    }

}


