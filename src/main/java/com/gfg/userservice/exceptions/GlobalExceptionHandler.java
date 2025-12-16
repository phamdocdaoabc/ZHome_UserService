package com.gfg.userservice.exceptions;

import com.gfg.userservice.constant.MessageUtil;
import com.gfg.userservice.domain.dto.base.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.UUID;


@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private MessageUtil messageUtil;

    @ExceptionHandler(AppException.class)
        public ResponseEntity<ApiResponse<?>> handleAppException(AppException ex) {
            String message = messageUtil.getMessage(ex.getErrorCode(), ex.getArgs());
            ApiResponse<?> response = ApiResponse.builder()
                    .message(message)
                    .traceId(UUID.randomUUID().toString())
                    .data(Map.of("errorCode", ex.getErrorCode()))
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }
}

