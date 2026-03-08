package com.gfg.userservice.service.serviceImpl;

import com.gfg.userservice.constant.ErrorCodes;
import com.gfg.userservice.domain.dto.postpackage.PostPackageDTO;
import com.gfg.userservice.domain.entity.PostPackageEntity;
import com.gfg.userservice.domain.entity.PostPackageMapEntity;
import com.gfg.userservice.domain.enums.PackageType;
import com.gfg.userservice.domain.enums.PostPackageStatus;
import com.gfg.userservice.exceptions.AppException;
import com.gfg.userservice.mapper.PostPackageMapper;
import com.gfg.userservice.repository.PostPackageMapRepository;
import com.gfg.userservice.repository.PostPackageRepository;
import com.gfg.userservice.security.SecurityUtils;
import com.gfg.userservice.service.PostPackageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PostPackageServiceImpl implements PostPackageService {
    private final PostPackageRepository postPackageRepository;
    private final PostPackageMapper postPackageMapper;
    private final PostPackageMapRepository postPackageMapRepository;

    @Override
    @Transactional
    public Long create(PostPackageDTO dto) {
        PostPackageEntity postPackageEntity = postPackageRepository.save(postPackageMapper.toEnity(dto));
        return postPackageEntity.getId();
    }

    @Override
    @Transactional
    public Long update(PostPackageDTO dto) {
        PostPackageEntity postPackageEntity = postPackageRepository.findById(dto.getId())
                .orElseThrow(() -> new AppException(ErrorCodes.DATA_Not_EXITS));
        postPackageMapper.updateEntity(dto, postPackageEntity);
        postPackageRepository.save(postPackageEntity);
        return postPackageEntity.getId();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        postPackageRepository.deleteById(id);
    }

    @Override
    public PostPackageDTO findById(Long id) {
        PostPackageEntity postPackageEntity = postPackageRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCodes.DATA_Not_EXITS));
        return postPackageMapper.toDTO(postPackageEntity);
    }

    @Override
    public List<PostPackageDTO> findAll() {
        List<PostPackageEntity> postPackageEntities = postPackageRepository.findAll();
        List<PostPackageDTO> postPackageDTOS = postPackageMapper.toDTOList(postPackageEntities);
        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCodes.USER_016));
        PostPackageMapEntity postPackageMapEntity = postPackageMapRepository.findByUserIdAndStatus(userId, PostPackageStatus.ACTIVE);
        // check user đang có gói không
        if (postPackageMapEntity != null) {
            for (PostPackageDTO postPackageDTO : postPackageDTOS) {
                if (postPackageDTO.getId().equals(postPackageMapEntity.getPackageId())) {
                    postPackageDTO.setIsCurrentActive(true);
                    postPackageDTO.setIsRegisterable(false);
                } else {
                    postPackageDTO.setIsCurrentActive(false);
                    postPackageDTO.setIsRegisterable(false);
                }
            }
            return postPackageDTOS;
        }
        // check user đã dùng gói basic trong tháng chưa
        LocalDateTime now = LocalDateTime.now(); // Lấy thời gian hiện tại
        LocalDateTime startOfMonth = now.withDayOfMonth(1).with(LocalTime.MIN); // Ngày 1 lúc 00:00:00
        LocalDateTime endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX); // Ngày cuối tháng lúc 23:59:59
        long countBasic = postPackageMapRepository.countBasicPackageInMonth(userId, startOfMonth, endOfMonth);
        if (countBasic > 0) {
            for (PostPackageDTO postPackageDTO : postPackageDTOS) {
                if (postPackageDTO.getPackageType().equals(PackageType.BASIC)) {
                    postPackageDTO.setIsCurrentActive(false);
                    postPackageDTO.setIsRegisterable(false);
                } else {
                    postPackageDTO.setIsCurrentActive(false);
                    postPackageDTO.setIsRegisterable(true);
                }
            }
            return  postPackageDTOS;
        }
        // Th 3
        for (PostPackageDTO postPackageDTO : postPackageDTOS) {
            postPackageDTO.setIsCurrentActive(false);
            postPackageDTO.setIsRegisterable(true);
        }
        return  postPackageDTOS;
    }
}
