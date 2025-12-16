package com.gfg.userservice.controller;


import com.gfg.userservice.domain.dto.base.ApiResponse;
import com.gfg.userservice.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {
    private final UploadService uploadService;

    @PostMapping("/image")
    public ApiResponse<?> uploadFile(@RequestPart("file") MultipartFile file,
                                     @RequestParam(value = "folder", defaultValue = "zhome") String folder) {
        String imageUrl = uploadService.uploadImage(file, folder);

        // Trả về JSON chứa URL
        return ApiResponse.builder()
                .message("Upload successful")
                .traceId(UUID.randomUUID().toString())
                .data(imageUrl)
                .build();
    }

    @PostMapping("/images")
    public ApiResponse<?> uploadFiles(@RequestPart(value = "files", required = false) MultipartFile[] files,
                                      @RequestParam(value = "folder", defaultValue = "zhome") String folder) {

        if (files == null || files.length == 0) {
            return ApiResponse.builder()
                    .message("No files provided")
                    .traceId(UUID.randomUUID().toString())
                    .data(null)
                    .build();
        }

        List<String> urls = uploadService.uploadMultipleImages(List.of(files), folder);

        return ApiResponse.builder()
                .message("Upload successful")
                .traceId(UUID.randomUUID().toString())
                .data(urls)
                .build();
    }
}
