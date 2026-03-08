package com.gfg.userservice.service.serviceImpl;

import com.gfg.userservice.constant.ErrorCodes;
import com.gfg.userservice.domain.dto.authentication.*;
import com.gfg.userservice.domain.dto.user.UserInfoDTO;
import com.gfg.userservice.domain.entity.CredentialEntity;
import com.gfg.userservice.domain.entity.UserEntity;
import com.gfg.userservice.domain.entity.VerificationTokenEntity;
import com.gfg.userservice.domain.enums.RoleBase;
import com.gfg.userservice.exceptions.AppException;
import com.gfg.userservice.repository.CredentialRepository;
import com.gfg.userservice.repository.UserRepository;
import com.gfg.userservice.repository.VerificationTokenRepository;
import com.gfg.userservice.security.SecurityUtils;
import com.gfg.userservice.service.AuthenticationService;
import com.gfg.userservice.service.EmailService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final CredentialRepository credentialRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final RSAPublicKey publicKey;

    @NonFinal
    @Value("${jwt.expiration}")
    protected Long EXPIRATION;

    @NonFinal
    @Value("${jwt.refresh-expiration}")
    protected Long REFRESH_EXPIRATION;

    @Override
    public LoginResponse authenticate(LoginDTO request) {
        // check userName
        CredentialEntity credentialEntity = credentialRepository.findByUserName(request.getUserName())
                .orElseThrow(() -> new AppException(ErrorCodes.CREDENTIAL_NOT_FOUND));

        // check password
        if (!passwordEncoder.matches(request.getPassword(), credentialEntity.getPassword())) {
            throw new AppException(ErrorCodes.USER_008);
        }

        // Kiểm tra trạng thái tài khoản đã kích hoạt chưa
        if (!credentialEntity.getIsEnabled()) {
            throw new AppException(ErrorCodes.USER_011);
        }

        // Kiểm tra tài khoản có bị khóa không
        if (!credentialEntity.getIsAccountNonLocked()) {
            throw new AppException(ErrorCodes.USER_012);
        }
        UserEntity userEntity = userRepository.findById(credentialEntity.getUserId())
                .orElseThrow(() -> new AppException(ErrorCodes.USER_NOT_FOUND));

        // 1. Sinh Access Token (Ngắn hạn)
        var accessToken = generateToken(credentialEntity, EXPIRATION);

        // 2. Sinh Refresh Token (Dài hạn)
        var refreshToken = generateToken(credentialEntity, REFRESH_EXPIRATION);
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .islogin(true)
                .user(UserInfoDTO.builder()
                        .id(userEntity.getId())
                        .fullName(userEntity.getFullName())
                        .imageUrl(userEntity.getImageUrl())
                        .address(userEntity.getAddress())
                        .username(credentialEntity.getUserName())
                        .role(credentialEntity.getRole())
                        .build())
                .build();
    }

    private String generateToken(CredentialEntity credentialEntity, long durationSeconds) {
        Instant now = Instant.now();
        // Xây dựng scope từ Roles
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (Objects.nonNull(credentialEntity.getRole())) {
            stringJoiner.add("ROLE_" + credentialEntity.getRole());
        }

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("UserService")
                .issuedAt(now)
                // Hết hạn dựa trên tham số truyền vào
                .expiresAt(now.plus(durationSeconds, ChronoUnit.SECONDS))
                .subject(credentialEntity.getUserName())
                .id(UUID.randomUUID().toString())
                .claim("scope", stringJoiner.toString())
                .claim("userId", credentialEntity.getUserId())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private SignedJWT verifyToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new RSASSAVerifier(publicKey);

            if (!signedJWT.verify(verifier)) throw new AppException(ErrorCodes.USER_009);

            // Check thời gian hết hạn chuẩn trong token
            Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (expiryTime.before(new Date())) throw new AppException(ErrorCodes.INVALID_TOKEN); // Token Expired

            // Check xem token có nằm trong blacklist (đã logout hoặc đã refresh) chưa
            if (verificationTokenRepository.existsByVerifToken(signedJWT.getJWTClaimsSet().getJWTID())) {
                throw new AppException(ErrorCodes.INVALID_TOKEN); // Token Invalid/Revoked
            }

            return signedJWT;
        } catch (ParseException | JOSEException e) {
            throw new AppException(ErrorCodes.INVALID_TOKEN);
        }
    }

    @Override
    @Transactional
    public void logout(String token) {
        try {
            // Verify để lấy ID và Expiry, dù hết hạn cũng parse để lấy ID blacklist
            var signedJWT = verifyToken(token);
            String jit = signedJWT.getJWTClaimsSet().getJWTID();
            Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

            verificationTokenRepository.save(VerificationTokenEntity.builder()
                    .verifToken(jit)
                    .expireDate(expiryTime)
                    .build());
        } catch (ParseException e) {
            log.info("Token format invalid");
        }
    }

    @Override
    public LoginResponse refreshToken(TokenDTO request) {
        try {
            // 1. Verify Refresh Token (Token đầu vào phải là Refresh Token)
            var signedJWT = verifyToken(request.getToken());
            // 2. Lấy JIT (ID của token) và hạn sử dụng
            var jit = signedJWT.getJWTClaimsSet().getJWTID();
            var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

            // 3. Token Rotation: Blacklist cái Refresh Token cũ này ngay lập tức
            // Để đảm bảo nó không được dùng lần 2.
            verificationTokenRepository.save(VerificationTokenEntity.builder()
                    .verifToken(jit)
                    .expireDate(expiryTime)
                    .build());

            String username = signedJWT.getJWTClaimsSet().getSubject();
            CredentialEntity credentialEntity = credentialRepository.findByUserName(username)
                    .orElseThrow(() -> new AppException(ErrorCodes.CREDENTIAL_NOT_FOUND));

            UserEntity userEntity = userRepository.findById(credentialEntity.getUserId())
                    .orElseThrow(() -> new AppException(ErrorCodes.USER_NOT_FOUND));

            // 5. Cấp cặp Token MỚI (New Access + New Refresh)
            var newAccessToken = generateToken(credentialEntity, EXPIRATION);
            var newRefreshToken = generateToken(credentialEntity, REFRESH_EXPIRATION);

            return LoginResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken) // Trả về Refresh Token mới
                    .islogin(true)
                    .build();
        } catch (ParseException e) {
            System.out.println("Lỗi: Chuỗi token không đúng định dạng (Rác)");
            throw new AppException(ErrorCodes.INVALID_TOKEN);
        }
    }

    @Override
    public IntrospectResponse introspect(TokenDTO introspectRequest) {
        var token = introspectRequest.getToken();
        boolean valid = true;
        try {
            verifyToken(token);
        } catch (AppException e) {
            valid = false;
        }
        return IntrospectResponse.builder()
                .valid(valid)
                .build();
    }

    @Override
    @Transactional
    public void activateAccount(String token) {
        try {
            // 1. Verify token (dùng public key check chữ ký & hạn dùng)
            // Lưu ý: Reuse hàm verifyToken public key ở bài trước
            SignedJWT signedJWT = verifyToken(token);

            // 2. Check xem có đúng là token kích hoạt không?
            String type = signedJWT.getJWTClaimsSet().getStringClaim("type");
            if (!"ACTIVATION".equals(type)) {
                throw new AppException(ErrorCodes.INVALID_TOKEN);
            }

            String userName = signedJWT.getJWTClaimsSet().getSubject();

            // 3. Logic nghiệp vụ (Giữ nguyên)
            CredentialEntity credentialEntity = credentialRepository.findByUserName(userName)
                    .orElseThrow(() -> new AppException(ErrorCodes.USER_NOT_FOUND));

            if (credentialEntity.getIsEnabled()) {
                throw new IllegalArgumentException("The account is already activated.");
            }

            credentialEntity.setIsEnabled(true); // Kích hoạt tài khoản
            credentialEntity.setIsAccountNonLocked(true); // Tài Khoản được mở Khóa
            credentialRepository.save(credentialEntity);
        } catch (Exception e) {
            throw new AppException(ErrorCodes.INVALID_TOKEN);
        }
    }

    public String generateActivationToken(String userName) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("RealEstateApp")
                .issuedAt(now)
                .expiresAt(now.plus(15, ChronoUnit.MINUTES)) // Chỉ sống 15 phút
                .subject(userName)
                .claim("type", "ACTIVATION") // Đánh dấu đây là token kích hoạt
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @Override
    @Transactional
    public void register(RegisterDTO registerDTO) {
        // Kiểm tra username/email đã tồn tại
        if (credentialRepository.existsByUserName(registerDTO.getUserName())) {
            throw new AppException(ErrorCodes.USER_EXISTS);
        }
        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new AppException(ErrorCodes.EMAIL_EXISTS);
        }
        // Kiểm tra độ dài của mật khẩu nhập vào
        if (registerDTO.getPassword().length() < 8) {
            throw new AppException(ErrorCodes.USER_014);
        }
        // Kiểm tra độ dài của userName nhập vào
        if (registerDTO.getUserName().length() < 8) {
            throw new AppException(ErrorCodes.USER_013);
        }
        // Lưu vào cơ sở dữ liệu
        UserEntity userSave = userRepository.save(UserEntity.builder()
                .fullName(registerDTO.getUserName())
                .email(registerDTO.getEmail())
                .build());

        // Tạo Credential
        CredentialEntity credentialEntity = CredentialEntity.builder()
                .userName(registerDTO.getUserName())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .role(RoleBase.USER)
                .isEnabled(false) // Tài khoản chưa kích hoạt
                .isAccountNonLocked(false) // Tài khoản chưa được mở khóa
                .userId(userSave.getId())
                .build();
        // Lưu vào cơ sở dữ liệu
        credentialRepository.save(credentialEntity);

        // Tạo token kích hoạt tài khoản
        String activationToken = generateActivationToken(credentialEntity.getUserName());

        // Gửi email kích hoạt tài khoản
        String emailContent = "Dear " + registerDTO.getUserName() + ",\n\n"
                + "Thank you for registering. Please click the link below to activate your account:\n"
                + "http://localhost:9050/user-service/api/account/activate?token=" + activationToken + "\n\n"
                + "Best regards,\nYour App Team";
        emailService.sendSimpleMessage(registerDTO.getEmail(), "Account ZHOME", emailContent);
    }

    @Override
    @Transactional
    public void resendActivationLink(String email) {
        // Tìm user theo email
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCodes.USER_NOT_FOUND));

        // Tìm credential theo userId
        CredentialEntity credentialEntity = credentialRepository.findByUserId(userEntity.getId())
                .orElseThrow(() -> new AppException(ErrorCodes.CREDENTIAL_NOT_FOUND));

        // Nếu tài khoản đã kích hoạt thì không gửi lại
        if (Boolean.TRUE.equals(credentialEntity.getIsEnabled())) {
            throw new AppException(ErrorCodes.ACCOUNT_ALREADY_ACTIVATED);
        }

        // Tạo token kích hoạt mới
        String activationToken = generateActivationToken(credentialEntity.getUserName());

        // Gửi email kích hoạt tài khoản
        String emailContent = "Dear " + credentialEntity.getUserName() + ",\n\n"
                + "Your previous activation link has expired. Please click the link below to activate your account:\n"
                + "http://localhost:9050/user-service/api/account/activate?token=" + activationToken + "\n\n"
                + "Best regards,\nYour App Team";

        emailService.sendSimpleMessage(email, "Resend Account Activation - ZHOME", emailContent);
    }

    @Override
    @Transactional
    public void forgotPassword(String email) {
        // 1. Kiểm tra Email có tồn tại không
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCodes.USER_NOT_FOUND));

        // 2. Tìm Credential tương ứng (nơi chứa mật khẩu)
        CredentialEntity credentialEntity = credentialRepository.findByUserId(userEntity.getId())
                .orElseThrow(() -> new AppException(ErrorCodes.CREDENTIAL_NOT_FOUND));

        // 3. Tạo mật khẩu ngẫu nhiên (Ví dụ lấy 8 ký tự đầu của UUID cho nhanh)
        String newRawPassword = UUID.randomUUID().toString().substring(0, 8);

        // 4. Mã hóa mật khẩu mới và Lưu xuống DB ngay lập tức
        credentialEntity.setPassword(passwordEncoder.encode(newRawPassword));
        credentialRepository.save(credentialEntity);

        // 5. Gửi email chứa mật khẩu thô (Raw Password) cho khách
        String emailContent = "Xin chào " + userEntity.getFullName() + ",\n\n"
                + "Mật khẩu mới của bạn là: " + newRawPassword + "\n\n"
                + "Vui lòng đăng nhập và đổi lại mật khẩu ngay lập tức để bảo mật thông tin.";

        emailService.sendSimpleMessage(email, "Cấp lại mật khẩu mới - ZHOME", emailContent);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordDTO changePasswordDTO) {
        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AppException(ErrorCodes.USER_016));

        // Tìm user theo username
        CredentialEntity credential = credentialRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCodes.CREDENTIAL_NOT_FOUND));

        // Kiểm tra mật khẩu hiện tại
        if (!passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), credential.getPassword())) {
            throw new AppException(ErrorCodes.USER_008);
        }

        // Kiểm tra độ dài của mật khẩu mới
        if (changePasswordDTO.getNewPassword().length() < 8) {
            throw new AppException(ErrorCodes.USER_014);
        }

        // Kiểm tra mật khẩu mới và xác nhận mật khẩu
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            throw new AppException(ErrorCodes.USER_018);
        }

        // Cập nhật mật khẩu mới
        credential.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        credentialRepository.save(credential);
    }
}
