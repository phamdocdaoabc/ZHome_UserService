package com.gfg.userservice.controller;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
public class JwkSetController {
    private final RSAKey rsaKey;

    public JwkSetController(RSAKey rsaKey) {
        this.rsaKey = rsaKey;
    }

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> keys() {
        // Chỉ trả về Public Key (Private key tự động bị ẩn bởi thư viện Nimbus)
        return new JWKSet(rsaKey).toJSONObject();
    }
}
