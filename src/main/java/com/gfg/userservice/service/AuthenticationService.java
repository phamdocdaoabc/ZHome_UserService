package com.gfg.userservice.service;


import com.gfg.userservice.domain.dto.authentication.*;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface AuthenticationService {
    LoginResponse authenticate(LoginDTO request);

    void logout(String token);

    LoginResponse refreshToken(TokenDTO request);

    IntrospectResponse introspect(TokenDTO introspectRequest);

    void activateAccount(String token);

    void register(RegisterDTO registerDTO);

    void resendActivationLink(String email);

    void forgotPassword(String email);

    void changePassword(ChangePasswordDTO changePasswordDTO);
}
