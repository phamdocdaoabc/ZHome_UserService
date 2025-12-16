package com.gfg.userservice.service;

import com.gfg.userservice.domain.dto.user.UserDTO;
import com.gfg.userservice.domain.dto.user.UserDetailDTO;
import com.gfg.userservice.domain.dto.user.UserFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UserDTO> findAll(UserFilter userFilter, Pageable pageable);
    UserDTO findById(Long userId);
    Long update(UserDetailDTO userDetailDTO);
    void deleteById(Long userId);
}
