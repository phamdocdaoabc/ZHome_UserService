package com.gfg.userservice.controller;

import com.gfg.userservice.domain.dto.base.ApiResponse;
import com.gfg.userservice.domain.dto.base.UrlResponse;
import com.gfg.userservice.domain.dto.postpackage.PostPackageFilterDTO;
import com.gfg.userservice.domain.dto.postpackage.PostPackageMapDTO;
import com.gfg.userservice.service.PostPackageMapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/post-package-map")
public class PostPackageMapController {
    private final PostPackageMapService postPackageMapService;

    @GetMapping("/detail")
    public ApiResponse<PostPackageMapDTO> getPostPackageMapDetail(@RequestParam Long id) {
        PostPackageMapDTO postPackageMapDTO = postPackageMapService.findById(id);
        return ApiResponse.<PostPackageMapDTO>builder()
                .message("Post Package Map Detail Retrieved")
                .traceId(UUID.randomUUID().toString())
                .data(postPackageMapDTO)
                .build();
    }

    @GetMapping
    public ApiResponse<Page<PostPackageMapDTO>> getAllPostPackageMaps(@ParameterObject PostPackageFilterDTO filterDTO,
                                                                       @ParameterObject Pageable pageable) {
        Page<PostPackageMapDTO> postPackageMaps = postPackageMapService.findAll(filterDTO, pageable);
        return ApiResponse.<Page<PostPackageMapDTO>>builder()
                .message("Post Package Maps Retrieved")
                .traceId(UUID.randomUUID().toString())
                .data(postPackageMaps)
                .build();
    }

    @GetMapping("/current")
    public ApiResponse<PostPackageMapDTO> getCurrentUserPostPackageMap() {
        PostPackageMapDTO postPackageMapDTO = postPackageMapService.findByUserId();
        return ApiResponse.<PostPackageMapDTO>builder()
                .message("Current User Post Package Map Retrieved")
                .traceId(UUID.randomUUID().toString())
                .data(postPackageMapDTO)
                .build();
    }

    @PostMapping
    public ApiResponse<UrlResponse<String>> createPostPackageMap(@RequestParam Long postPackageId) {
        return ApiResponse.<UrlResponse<String>>builder()
                .message("Post Package Map Created")
                .traceId(UUID.randomUUID().toString())
                .data(UrlResponse.<String>builder()
                        .url(postPackageMapService.create(postPackageId))
                        .build())
                .build();
    }

    @PutMapping("/cancel")
    public ApiResponse<Void> cancelCurrentPackage() {
        postPackageMapService.cancelled();
        return ApiResponse.<Void>builder()
                .message("Current package cancelled successfully")
                .traceId(UUID.randomUUID().toString())
                .build();
    }

    @PutMapping("/status")
    public ApiResponse<Void> updateStatus(@RequestParam Long id, @RequestParam Boolean statusActive) {
        postPackageMapService.updateStatus(id, statusActive);
        return ApiResponse.<Void>builder()
                .message("Post Package Map status updated successfully")
                .traceId(UUID.randomUUID().toString())
                .build();
    }

    @PutMapping("/job-update-status")
    public ApiResponse<Void> jobUpdateStatus() {
        postPackageMapService.jobUpdateStatus();
        return ApiResponse.<Void>builder()
                .message("Post Package Map statuses updated successfully by job")
                .traceId(UUID.randomUUID().toString())
                .build();
    }

    @PutMapping("/update-current-post-count")
    public ApiResponse<Void> updateCurrentPostCount(@RequestParam Long postPackageMapId) {
        postPackageMapService.updateCurrentPostCount(postPackageMapId);
        return ApiResponse.<Void>builder()
                .message("Current post count updated successfully")
                .traceId(UUID.randomUUID().toString())
                .build();
    }
}
