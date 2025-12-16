package com.gfg.userservice.controller;

import com.gfg.userservice.domain.dto.base.ApiResponse;
import com.gfg.userservice.domain.dto.base.IdsResponse;
import com.gfg.userservice.domain.dto.base.PageResponse;
import com.gfg.userservice.domain.dto.user.UserDTO;
import com.gfg.userservice.domain.dto.user.UserDetailDTO;
import com.gfg.userservice.domain.dto.user.UserFilter;
import com.gfg.userservice.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public ApiResponse<PageResponse<UserDTO>> findAll(@ParameterObject UserFilter userFilter,
                                                      @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<UserDTO> pageResult = userService.findAll(userFilter, pageable);
        return ApiResponse.<PageResponse<UserDTO>>builder()
                .message("Successfully")
                .traceId(UUID.randomUUID().toString()) // chuỗi UUID random
                .data(PageResponse.<UserDTO>builder()
                        .content(pageResult.getContent())
                        .page(pageResult.getNumber())
                        .size(pageResult.getSize())
                        .sort(pageable.getSort().toString())
                        .totalElements(pageResult.getTotalElements())
                        .totalPages(pageResult.getTotalPages())
                        .numberOfElements(pageResult.getNumberOfElements())
                        .build())
                .build();

    }

    @GetMapping("/{userId}")
    public ApiResponse<UserDTO> findById(@PathVariable("userId") @NotBlank(message = "Input must not blank") Long userId) {
        return ApiResponse.<UserDTO>builder()
                .message("Successfully")
                .traceId(UUID.randomUUID().toString()) // chuỗi UUID random
                .data(userService.findById(userId))
                .build();
    }

    @PutMapping
    public ApiResponse<IdsResponse<Long>> update(@RequestBody @NotNull(message = "Input must not null") @Valid UserDetailDTO userDetailDTO) {
        return ApiResponse.<IdsResponse<Long>>builder()
                .message("Successfully")
                .traceId(UUID.randomUUID().toString())
                .data(IdsResponse.<Long>builder()
                        .id(userService.update(userDetailDTO))
                        .build())
                .build();
    }


    @DeleteMapping("/{userId}")
    public ApiResponse<Boolean> deleteById(@PathVariable("userId") @NotBlank(message = "Input must not blank") @Valid Long userId) {
        userService.deleteById(userId);
        return ApiResponse.<Boolean>builder()
                .message("Successfully")
                .traceId(UUID.randomUUID().toString())
                .data(true)
                .build();
    }
}
