package com.gfg.userservice.service.serviceImpl;

import com.gfg.userservice.constant.ErrorCodes;
import com.gfg.userservice.domain.dto.postpackage.PostPackageFilterDTO;
import com.gfg.userservice.domain.dto.postpackage.PostPackageMapDTO;
import com.gfg.userservice.domain.dto.postpackage.PostPackageMapPage;
import com.gfg.userservice.domain.entity.PostPackageEntity;
import com.gfg.userservice.domain.entity.PostPackageMapEntity;
import com.gfg.userservice.domain.entity.UserEntity;
import com.gfg.userservice.domain.enums.PostPackageStatus;
import com.gfg.userservice.exceptions.AppException;
import com.gfg.userservice.mapper.PostPackageMapMapper;
import com.gfg.userservice.proxy.PaymentProxy;
import com.gfg.userservice.proxy.dto.VnpayRequest;
import com.gfg.userservice.repository.PostPackageMapRepository;
import com.gfg.userservice.repository.PostPackageRepository;
import com.gfg.userservice.repository.UserRepository;
import com.gfg.userservice.repository.specs.PostPackageMapSpecification;
import com.gfg.userservice.repository.specs.SpecificationUtils;
import com.gfg.userservice.security.SecurityUtils;
import com.gfg.userservice.service.PostPackageMapService;
import com.gfg.userservice.service.PostPackageService;
import com.gfg.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PostPackageMapServiceImpl implements PostPackageMapService {
    private final PostPackageMapRepository postPackageMapRepository;
    private final PostPackageMapMapper postPackageMapMapper;
    private final UserService userService;
    private final PostPackageService postPackageService;
    private final UserRepository userRepository;
    private final PostPackageRepository postPackageRepository;
    private final SecurityUtils securityUtils;
    private final PaymentProxy paymentProxy;

    @Override
    public PostPackageMapDTO findById(Long id) {
        PostPackageMapEntity postPackageMapEntity = postPackageMapRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCodes.DATA_Not_EXITS));
        PostPackageMapDTO postPackageMapDTO = postPackageMapMapper.toDTO(postPackageMapEntity);
        postPackageMapDTO.setUser(userService.findById(postPackageMapEntity.getUserId()));
        postPackageMapDTO.setPostPackage(postPackageService.findById(postPackageMapEntity.getId()));
        return postPackageMapDTO;
    }

    @Override
    public Page<PostPackageMapPage> findAll(PostPackageFilterDTO filterDTO, Pageable pageable) {
        // filter by Post Package fields
        if (Objects.nonNull(filterDTO.getPackageType())) {
            List<PostPackageEntity> postPackageEntities = postPackageRepository.findAllByPackageType(filterDTO.getPackageType());
            if (postPackageEntities.isEmpty()) {
                return Page.empty(pageable);
            }
            Set<Long> postPackageIds = postPackageEntities.stream()
                    .map(PostPackageEntity::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            filterDTO.setPackageIds(postPackageIds);
        }
        // filter by Post Package Map fields
        Specification<PostPackageMapEntity> spec = Specification.where(null);
        spec = SpecificationUtils.addIfNotEmpty(spec, filterDTO.getPackageIds(), PostPackageMapSpecification::hasPackageIds);
        spec = SpecificationUtils.addIfNotNull(spec, filterDTO.getStatus(), PostPackageMapSpecification::hasStatus);
        spec = SpecificationUtils.addIfNotNull(spec, filterDTO.getStartDate(), PostPackageMapSpecification::hasStartDate);
        Page<PostPackageMapEntity> postPackageMapEntityPage = postPackageMapRepository.findAll(spec, pageable);
        if (postPackageMapEntityPage.isEmpty()) {
            return Page.empty(pageable);
        }
        Set<Long> userIds = new HashSet<>();
        Set<Long> packageIds = new HashSet<>();
        postPackageMapEntityPage.forEach(entity -> {
            userIds.add(entity.getUserId());
            packageIds.add(entity.getPackageId());
        });
        Map<Long, UserEntity> userEntityMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(UserEntity::getId, userEntity -> userEntity));
        Map<Long, PostPackageEntity> postPackageEntityMap = postPackageRepository.findAllById(packageIds).stream()
                .collect(Collectors.toMap(PostPackageEntity::getId, postPackageEntity -> postPackageEntity));
        return postPackageMapEntityPage.map(entity -> {
            PostPackageMapPage dto = postPackageMapMapper.toPage(entity);
            UserEntity userEntity = userEntityMap.get(entity.getUserId());
            dto.setUserId(userEntity.getId());
            dto.setFullName(userEntity.getFullName());
            dto.setEmail(userEntity.getEmail());
            dto.setImageUrl(userEntity.getImageUrl());
            PostPackageEntity postPackageEntity = postPackageEntityMap.get(entity.getPackageId());
            dto.setPackageId(postPackageEntity.getId());
            dto.setPackageType(postPackageEntity.getPackageType());
            return dto;
        });
    }

    @Override
    public PostPackageMapDTO findByUserId() {
        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCodes.USER_016));
        PostPackageMapEntity postPackageMapEntity = postPackageMapRepository.findByUserIdAndStatus(userId, PostPackageStatus.ACTIVE);
        if (postPackageMapEntity != null) {
            PostPackageMapDTO postPackageMapDTO = postPackageMapMapper.toDTO(postPackageMapEntity);
            postPackageMapDTO.setUser(userService.findById(postPackageMapEntity.getUserId()));
            postPackageMapDTO.setPostPackage(postPackageService.findById(postPackageMapEntity.getPackageId()));
            return postPackageMapDTO;
        }
        return null;
    }

    @Override
    @Transactional
    public String create(Long postPackageId) {
        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCodes.USER_016));

        // check useriD đã có gói cước ACTIVE chưa
        PostPackageMapEntity existingActivePackage = postPackageMapRepository.findByUserIdAndStatus(userId, PostPackageStatus.ACTIVE);
        if (existingActivePackage != null) {
            throw new AppException(ErrorCodes.USER_017);
        }

        PostPackageEntity postPackageEntity = postPackageRepository.findById(postPackageId)
                .orElseThrow(() -> new AppException(ErrorCodes.DATA_Not_EXITS));

        // 2. Chuẩn bị dữ liệu ngày tháng
        LocalDateTime now = LocalDateTime.now(); // Lấy thời gian hiện tại

        // Tính EndDate = StartDate + DurationDays
        LocalDateTime calculatedEndDate = now.plusDays(postPackageEntity.getDurationDays());

        if (postPackageEntity.getPrice() <= 0){
            // Lưu PostPackageMapEntity
            PostPackageMapEntity postPackageMapEntity = postPackageMapRepository.save(PostPackageMapEntity.builder()
                    .userId(userId)
                    .packageId(postPackageId)
                    .status(PostPackageStatus.ACTIVE)
                    .startDate(now)
                    .endDate(calculatedEndDate)
                    .totalPostsAllowed(postPackageEntity.getListingLimit())
                    .currentPostCount(0L)
                    .build());
            return null;
        }

        // 3. Lưu PostPackageMapEntity
        PostPackageMapEntity postPackageMapEntity = postPackageMapRepository.save(PostPackageMapEntity.builder()
                .userId(userId)
                .packageId(postPackageId)
                .status(PostPackageStatus.PENDING)
                .startDate(now)
                .endDate(calculatedEndDate)
                .totalPostsAllowed(postPackageEntity.getListingLimit())
                .currentPostCount(0L)
                .build());

        // Create payment àn return vnpayUrl;
        return paymentProxy.createVnPayPayment(VnpayRequest.builder()
                .postPackageMapId(postPackageMapEntity.getId())
                .amount(postPackageEntity.getPrice())
                .orderInfo("Payment for post package: " + postPackageMapEntity.getId())
                .build());
    }

    @Override
    @Transactional
    public void cancelled() {
        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCodes.USER_016));
        PostPackageMapEntity activePackage = postPackageMapRepository.findByUserIdAndStatus(userId, PostPackageStatus.ACTIVE);
        if (activePackage != null) {
            activePackage.setStatus(PostPackageStatus.CANCELLED);
            postPackageMapRepository.save(activePackage);
        }
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Boolean statusActive) {
        PostPackageMapEntity postPackageMapEntity = postPackageMapRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCodes.DATA_Not_EXITS));
        if (statusActive) {
            postPackageMapEntity.setStatus(PostPackageStatus.ACTIVE);
        } else {
            postPackageMapEntity.setStatus(PostPackageStatus.FAILED);
        }
        postPackageMapRepository.save(postPackageMapEntity);
    }
}
