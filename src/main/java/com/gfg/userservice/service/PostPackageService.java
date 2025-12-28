package com.gfg.userservice.service;

import com.gfg.userservice.domain.dto.postpackage.PostPackageDTO;

import java.util.List;

public interface PostPackageService {
    Long create(PostPackageDTO dto);
    Long update(PostPackageDTO dto);
    void delete(Long id);
    PostPackageDTO findById(Long id);
    List<PostPackageDTO> findAll();
}
