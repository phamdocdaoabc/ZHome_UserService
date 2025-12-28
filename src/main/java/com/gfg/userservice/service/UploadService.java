package com.gfg.userservice.service;

import com.gfg.userservice.domain.dto.base.ImageUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UploadService {
    ImageUploadResponse uploadImage(MultipartFile file, String folderName);

    void deleteImage(String fileId);

    List<ImageUploadResponse> uploadMultipleImages(List<MultipartFile> files, String folderName);

    void deleteMultipleImages(List<String> fileIds);
}
