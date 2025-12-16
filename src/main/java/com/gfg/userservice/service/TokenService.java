package com.gfg.userservice.service;

public interface TokenService {
    String generateToken(String username, String role);
}
