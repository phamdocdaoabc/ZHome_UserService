package com.gfg.userservice.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import feign.RequestInterceptor;
import feign.RequestTemplate;

@Component
public class FeignClientInterceptor implements RequestInterceptor {
    // Tự động tiêm Bearer Token từ request hiện tại vào Feign Request
    @Override
    public void apply(RequestTemplate requestTemplate) {
        // 1. Lấy Attributes của Request hiện tại (đang được xử lý ở Controller)
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            // 2. Lấy Header Authorization (chứa Bearer Token)
            String authorizationHeader = request.getHeader("Authorization");

            // 3. Nếu có Token, bơm nó vào Header của Feign Request
            if (authorizationHeader != null) {
                requestTemplate.header("Authorization", authorizationHeader);
            }
        }
    }
}
