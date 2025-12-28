package com.gfg.userservice.service.serviceImpl;

import com.gfg.userservice.domain.dto.base.ImageUploadResponse;
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
    public ImageUploadResponse uploadImage(MultipartFile file, String folderName) {
        try {
            // 1. Chuyển MultipartFile sang mảng byte
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // 2. Dùng Thumbnailator để nén
            Thumbnails.of(file.getInputStream())
                    .size(1024, 1024)      // Quan trọng: Resize về tối đa 1200px (đủ nét cho web)
                    .outputQuality(0.7)    // Giảm chất lượng xuống 80% (mắt thường ko thấy khác)
                    .toOutputStream(outputStream);

            byte[] compressedBytes = outputStream.toByteArray();

            // --- BƯỚC UPLOAD LÊN IMAGEKIT ---

            FileCreateRequest fileCreateRequest = new FileCreateRequest(compressedBytes, file.getOriginalFilename());
            if (folderName != null) {
                fileCreateRequest.setFolder(folderName);
            }

            Result result = imageKit.upload(fileCreateRequest);
            return ImageUploadResponse.builder()
                    .fileId(result.getFileId())  // Lấy fileId
                    .url(result.getUrl())        // Lấy URL
                    .name(result.getName())
                    .thumbnailUrl(result.getThumbnail())
                    .height(result.getHeight())
                    .width(result.getWidth())
                    .build();

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
    public List<ImageUploadResponse> uploadMultipleImages(List<MultipartFile> files, String folderName) {
        return files.parallelStream() // Dùng parallelStream để chạy đa luồng
                .map(file -> uploadImage(file, folderName)) // Gọi hàm upload lẻ
                .collect(Collectors.toList());
    }

    @Override
    public void deleteMultipleImages(List<String> fileIds) {
        // 1. Kiểm tra đầu vào
        if (fileIds == null || fileIds.isEmpty()) {
            return;
        }
        try {
            imageKit.bulkDeleteFiles(fileIds);
        } catch (Exception e) {
            // Log lỗi nếu cần (khuyên dùng Logger thay vì printStackTrace)
            throw new RuntimeException("Lỗi xóa ảnh hàng loạt: " + e.getMessage());
        }
    }
}
