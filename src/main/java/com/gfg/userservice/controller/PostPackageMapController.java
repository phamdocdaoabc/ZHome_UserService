package com.gfg.userservice.controller;

import com.gfg.userservice.domain.dto.base.ApiResponse;
import com.gfg.userservice.domain.dto.base.UrlResponse;
import com.gfg.userservice.service.PostPackageMapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/post-package-map")
public class PostPackageMapController {
    private final PostPackageMapService postPackageMapService;

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
}
