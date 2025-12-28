package com.gfg.userservice.controller;

import com.gfg.userservice.domain.dto.base.ApiResponse;
import com.gfg.userservice.domain.dto.base.IdsResponse;
import com.gfg.userservice.domain.dto.postpackage.PostPackageDTO;
import com.gfg.userservice.service.PostPackageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/post-packages")
public class PostPackageController {
    private final PostPackageService postPackageService;

    @PostMapping
    public ApiResponse<IdsResponse<Long>> create(@RequestBody PostPackageDTO dto) {
        return ApiResponse.<IdsResponse<Long>>builder()
                .message("Successfully")
                .traceId(UUID.randomUUID().toString())
                .data(IdsResponse.<Long>builder()
                        .id(postPackageService.create(dto))
                        .build())
                .build();
    }

    @PutMapping
    public ApiResponse<IdsResponse<Long>> update(@RequestBody PostPackageDTO dto) {
        return ApiResponse.<IdsResponse<Long>>builder()
                .message("Successfully")
                .traceId(UUID.randomUUID().toString())
                .data(IdsResponse.<Long>builder()
                        .id(postPackageService.update(dto))
                        .build())
                .build();
    }

    @DeleteMapping
    public ApiResponse<Void> delete(@RequestParam Long id) {
        postPackageService.delete(id);
        return ApiResponse.<Void>builder()
                .message("Successfully")
                .traceId(UUID.randomUUID().toString())
                .build();
    }

    @GetMapping("/detail")
    public ApiResponse<PostPackageDTO> findById(@RequestParam Long id) {
        return ApiResponse.<PostPackageDTO>builder()
                .message("Successfully")
                .traceId(UUID.randomUUID().toString())
                .data(postPackageService.findById(id))
                .build();
    }

    @GetMapping
    public ApiResponse<List<PostPackageDTO>> findAll() {
        return ApiResponse.<List<PostPackageDTO>>builder()
                .message("Successfully")
                .traceId(UUID.randomUUID().toString())
                .data(postPackageService.findAll())
                .build();
    }
}
