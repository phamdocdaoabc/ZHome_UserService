package com.gfg.userservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UploadService {
    String uploadImage(MultipartFile file, String folderName);
    void deleteImage(String fileId);
    List<String> uploadMultipleImages(List<MultipartFile> files, String folderName);
}
