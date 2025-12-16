package com.gfg.userservice.controller;

import com.gfg.userservice.domain.dto.base.ApiResponse;
import com.gfg.userservice.domain.dto.user.CredentialDTO;
import com.gfg.userservice.domain.dto.user.CredentialStatusDTO;
import com.gfg.userservice.response.ResponseCollectionDTO;
import com.gfg.userservice.service.CredentialService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/credentials")
public class CredentialController {

    private final CredentialService credentialService;

    @GetMapping
    public ResponseEntity<ResponseCollectionDTO<CredentialDTO>>findAll() {
        return ResponseEntity.ok(new ResponseCollectionDTO<>(this.credentialService.findAll()));
    }

    @GetMapping("/{credentialId}")
    public ResponseEntity<CredentialDTO> findById(@PathVariable("credentialId") @NotBlank(message = "Input must not be blank") @Valid Long credentialId) {
        return ResponseEntity.ok(this.credentialService.findById(credentialId));
    }

    @PostMapping
    public ResponseEntity<CredentialDTO> save(@RequestBody @NotNull(message = "input must not be blank") @Valid final CredentialDTO credentialDTO) {
        return ResponseEntity.ok(this.credentialService.save(credentialDTO));
    }

    @PutMapping
    public ResponseEntity<CredentialDTO> update(@RequestBody @NotNull(message = "Input must not NULL") @Valid final CredentialDTO credentialDto) {
        return ResponseEntity.ok(this.credentialService.update(credentialDto));
    }

    @DeleteMapping("/{credentialId}")
    public ResponseEntity<Boolean> deleteById(@PathVariable("credentialId") @NotBlank(message = "Input must not blank") @Valid Long credentialId) {
        this.credentialService.deleteById(credentialId);
        return ResponseEntity.ok(true);
    }

    @PutMapping("/status")
    public ApiResponse<Boolean> updateStatus(@RequestBody @Valid CredentialStatusDTO credentialStatusDTO) {
        credentialService.updateStatusLocked(credentialStatusDTO);
        return ApiResponse.<Boolean>builder()
                .message("Successfully")
                .traceId(UUID.randomUUID().toString())
                .data(true)
                .build();
    }
}
