package com.gfg.userservice.service;

import com.gfg.userservice.domain.dto.postpackage.PostPackageFilterDTO;
import com.gfg.userservice.domain.dto.postpackage.PostPackageMapDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostPackageMapService {
    PostPackageMapDTO findById(Long id);

    Page<PostPackageMapDTO> findAll(PostPackageFilterDTO filterDTO, Pageable pageable);

    PostPackageMapDTO findByUserId();

    String create(Long postPackageId);

    // Hủy gói hiện tại
    void cancelled();

    void updateStatus(Long id, Boolean statusActive);

    void jobUpdateStatus();

    void updateCurrentPostCount(Long postPackageMapId);
}
