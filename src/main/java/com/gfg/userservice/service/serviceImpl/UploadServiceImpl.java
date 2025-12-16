package com.gfg.userservice.service.serviceImpl;

import com.gfg.userservice.service.UploadService;
import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.results.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UploadServiceImpl implements UploadService {
    private final ImageKit imageKit;

    @Override
    public String uploadImage(MultipartFile file, String folderName) {
        try {
            // 1. Chuyển MultipartFile sang mảng byte
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // 2. Dùng Thumbnailator để nén
            Thumbnails.of(file.getInputStream())
                    .size(1200, 1200)      // Quan trọng: Resize về tối đa 1200px (đủ nét cho web)
                    .outputQuality(0.8)    // Giảm chất lượng xuống 80% (mắt thường ko thấy khác)
                    .toOutputStream(outputStream);

            byte[] compressedBytes = outputStream.toByteArray();

            // --- BƯỚC UPLOAD LÊN IMAGEKIT ---

            FileCreateRequest fileCreateRequest = new FileCreateRequest(compressedBytes, file.getOriginalFilename());
            if (folderName != null) {
                fileCreateRequest.setFolder(folderName);
            }

            Result result = imageKit.upload(fileCreateRequest);
            return result.getUrl();

        } catch (Exception e) {
            throw new RuntimeException("Lỗi xử lý ảnh: " + e.getMessage());
        }
    }

    @Override
    public void deleteImage(String fileId) {
        try {
            imageKit.deleteFile(fileId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> uploadMultipleImages(List<MultipartFile> files, String folderName) {
        return files.parallelStream() // Dùng parallelStream để chạy đa luồng
                .map(file -> uploadImage(file, folderName)) // Gọi hàm upload lẻ
                .collect(Collectors.toList());
    }
}
