package com.gfg.userservice.service.serviceImpl;

import com.gfg.userservice.constant.ErrorCodes;
import com.gfg.userservice.domain.dto.postpackage.PostPackageDTO;
import com.gfg.userservice.domain.entity.PostPackageEntity;
import com.gfg.userservice.exceptions.AppException;
import com.gfg.userservice.mapper.PostPackageMapper;
import com.gfg.userservice.repository.PostPackageRepository;
import com.gfg.userservice.service.PostPackageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PostPackageServiceImpl implements PostPackageService {
    private final PostPackageRepository postPackageRepository;
    private final PostPackageMapper postPackageMapper;

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
        return postPackageMapper.toDTOList(postPackageEntities);
    }
}
