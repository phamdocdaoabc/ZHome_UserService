package com.gfg.userservice.service.serviceImpl;

import com.gfg.userservice.constant.ErrorCodes;
import com.gfg.userservice.domain.dto.postpackage.PostPackageFilterDTO;
import com.gfg.userservice.domain.dto.postpackage.PostPackageMapDTO;
import com.gfg.userservice.domain.dto.user.UserMapDTO;
import com.gfg.userservice.domain.entity.CredentialEntity;
import com.gfg.userservice.domain.entity.PostPackageEntity;
import com.gfg.userservice.domain.entity.PostPackageMapEntity;
import com.gfg.userservice.domain.entity.UserEntity;
import com.gfg.userservice.domain.enums.PackageType;
import com.gfg.userservice.domain.enums.PostPackageStatus;
import com.gfg.userservice.domain.enums.RoleBase;
import com.gfg.userservice.exceptions.AppException;
import com.gfg.userservice.mapper.PostPackageMapMapper;
import com.gfg.userservice.mapper.PostPackageMapper;
import com.gfg.userservice.proxy.PaymentProxy;
import com.gfg.userservice.proxy.dto.VnpayRequest;
import com.gfg.userservice.repository.CredentialRepository;
import com.gfg.userservice.repository.PostPackageMapRepository;
import com.gfg.userservice.repository.PostPackageRepository;
import com.gfg.userservice.repository.UserRepository;
import com.gfg.userservice.repository.specs.PostPackageMapSpecification;
import com.gfg.userservice.repository.specs.SpecificationUtils;
import com.gfg.userservice.security.SecurityUtils;
import com.gfg.userservice.service.PostPackageMapService;
import com.gfg.userservice.service.PostPackageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PostPackageMapServiceImpl implements PostPackageMapService {
    private final PostPackageMapRepository postPackageMapRepository;
    private final PostPackageMapMapper postPackageMapMapper;
    private final PostPackageService postPackageService;
    private final UserRepository userRepository;
    private final PostPackageRepository postPackageRepository;
    private final SecurityUtils securityUtils;
    private final PaymentProxy paymentProxy;
    private final CredentialRepository credentialRepository;
    private final PostPackageMapper postPackageMapper;

    @Override
    public PostPackageMapDTO findById(Long id) {
        PostPackageMapEntity postPackageMapEntity = postPackageMapRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCodes.DATA_Not_EXITS));
        checkAndExpire(postPackageMapEntity);

        PostPackageMapDTO postPackageMapDTO = postPackageMapMapper.toDTO(postPackageMapEntity);
        UserEntity userEntity = userRepository.findById(postPackageMapEntity.getUserId())
                .orElseThrow(() -> new AppException(ErrorCodes.USER_NOT_FOUND));
        postPackageMapDTO.setUser(UserMapDTO.builder()
                .id(userEntity.getId())
                .fullName(userEntity.getFullName())
                .address(userEntity.getAddress())
                .imageUrl(userEntity.getImageUrl())
                .build());
        postPackageMapDTO.setPostPackage(postPackageService.findById(postPackageMapEntity.getId()));
        return postPackageMapDTO;
    }

    private void checkAndExpire(PostPackageMapEntity entity) {
        if (PostPackageStatus.ACTIVE.equals(entity.getStatus())
                && entity.getEndDate() != null
                && entity.getEndDate().isBefore(LocalDateTime.now())) {

            entity.setStatus(PostPackageStatus.EXPIRED);
            postPackageMapRepository.save(entity); // Cập nhật DB ngay lập tức
        }
    }

    @Override
    @Transactional
    public Page<PostPackageMapDTO> findAll(PostPackageFilterDTO filterDTO, Pageable pageable) {
        // check hết hạn
        postPackageMapRepository.findAll().forEach(this::checkAndExpire);
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
        if (Objects.nonNull(filterDTO.getIsCurrent()) && filterDTO.getIsCurrent()) {
            Long userId = SecurityUtils.getCurrentUserId()
                    .orElseThrow(() -> new AppException(ErrorCodes.USER_016));
            spec = SpecificationUtils.addIfNotNull(spec, userId, PostPackageMapSpecification::hasUserId);
        }
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
            PostPackageMapDTO dto = postPackageMapMapper.toDTO(entity);
            UserEntity userEntity = userEntityMap.get(entity.getUserId());
            dto.setUser(UserMapDTO.builder()
                    .id(userEntity.getId())
                    .fullName(userEntity.getFullName())
                    .address(userEntity.getAddress())
                    .imageUrl(userEntity.getImageUrl())
                    .build());
            PostPackageEntity postPackageEntity = postPackageEntityMap.get(entity.getPackageId());
            dto.setPostPackage(postPackageMapper.toDTO(postPackageEntityMap.get(entity.getPackageId())));
            return dto;
        });
    }

    @Override
    @Transactional
    public PostPackageMapDTO findByUserId() {
        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCodes.USER_016));
        PostPackageMapEntity postPackageMapEntity = postPackageMapRepository.findByUserIdAndStatus(userId, PostPackageStatus.ACTIVE);
        checkAndExpire(postPackageMapEntity);
        if (postPackageMapEntity.getStatus().equals(PostPackageStatus.ACTIVE)) {
            PostPackageMapDTO postPackageMapDTO = postPackageMapMapper.toDTO(postPackageMapEntity);
            UserEntity userEntity = userRepository.findById(postPackageMapEntity.getUserId())
                    .orElseThrow(() -> new AppException(ErrorCodes.USER_NOT_FOUND));
            postPackageMapDTO.setUser(UserMapDTO.builder()
                    .id(userEntity.getId())
                    .fullName(userEntity.getFullName())
                    .address(userEntity.getAddress())
                    .imageUrl(userEntity.getImageUrl())
                    .build());
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

        // 2. Chuẩn bị dữ liệu ngày tháng
        LocalDateTime now = LocalDateTime.now(); // Lấy thời gian hiện tại

        PostPackageEntity postPackageEntity = postPackageRepository.findById(postPackageId)
                .orElseThrow(() -> new AppException(ErrorCodes.DATA_Not_EXITS));
        if (postPackageEntity.getPackageType().equals(PackageType.BASIC)) {
            // Tính thời điểm bắt đầu tháng và kết thúc tháng này
            LocalDateTime startOfMonth = now.withDayOfMonth(1).with(LocalTime.MIN); // Ngày 1 lúc 00:00:00
            LocalDateTime endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX); // Ngày cuối tháng lúc 23:59:59

            Long countBasic = postPackageMapRepository.countBasicPackageInMonth(userId, startOfMonth, endOfMonth);
            if (countBasic > 0) {
                throw new AppException(ErrorCodes.USER_019);
            }
        }

        // Tính EndDate = StartDate + DurationDays
        LocalDateTime calculatedEndDate = now.plusDays(postPackageEntity.getDurationDays());

        if (postPackageEntity.getPrice() <= 0) {
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
            CredentialEntity credentialEntity = credentialRepository.findByUserId(userId)
                    .orElseThrow(() -> new AppException(ErrorCodes.CREDENTIAL_NOT_FOUND));
            credentialEntity.setRole(RoleBase.OWNER);
            credentialRepository.save(credentialEntity);
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
            CredentialEntity credentialEntity = credentialRepository.findByUserId(postPackageMapEntity.getUserId())
                    .orElseThrow(() -> new AppException(ErrorCodes.CREDENTIAL_NOT_FOUND));
            credentialEntity.setRole(RoleBase.OWNER);
            credentialRepository.save(credentialEntity);
        } else {
            postPackageMapEntity.setStatus(PostPackageStatus.FAILED);
        }
        postPackageMapRepository.save(postPackageMapEntity);
    }

    @Override
    @Transactional
    public void jobUpdateStatus() {
        List<PostPackageMapEntity> expiredPackages = postPackageMapRepository.findAllByEndDateBeforeAndStatus(LocalDateTime.now(), PostPackageStatus.ACTIVE);
        if (expiredPackages.isEmpty()) {
            return;
        }
        Set<Long> userIds = expiredPackages.stream()
                .map(PostPackageMapEntity::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, CredentialEntity> credentialEntityMap = credentialRepository.findAllByUserIdIn(userIds).stream()
                .collect(Collectors.toMap(CredentialEntity::getUserId, credentialEntity -> credentialEntity));
        List<CredentialEntity> credentialEntityUpdate = new ArrayList<>();
        List<PostPackageMapEntity> postPackageMapEntitiesToUpdate = new ArrayList<>();
        for (PostPackageMapEntity postPackageMapEntity : expiredPackages) {
            postPackageMapEntity.setStatus(PostPackageStatus.EXPIRED);
            postPackageMapEntitiesToUpdate.add(postPackageMapEntity);
            CredentialEntity credentialEntity = credentialEntityMap.get(postPackageMapEntity.getUserId());
            if (credentialEntity != null) {
                credentialEntity.setRole(RoleBase.USER);
                credentialEntityUpdate.add(credentialEntity);
            }
        }
        postPackageMapRepository.saveAll(postPackageMapEntitiesToUpdate);
        credentialRepository.saveAll(credentialEntityUpdate);
    }

    @Override
    @Transactional
    public void updateCurrentPostCount(Long postPackageMapId) {
        PostPackageMapEntity postPackageMapEntity = postPackageMapRepository.findById(postPackageMapId)
                .orElseThrow(() -> new AppException(ErrorCodes.DATA_Not_EXITS));
        postPackageMapEntity.setCurrentPostCount(postPackageMapEntity.getCurrentPostCount() + 1);
        postPackageMapRepository.save(postPackageMapEntity);
    }
}
