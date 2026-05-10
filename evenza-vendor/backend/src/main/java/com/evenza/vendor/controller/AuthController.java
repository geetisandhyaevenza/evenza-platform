package com.evenza.vendor.controller;

import com.evenza.vendor.security.JwtUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    // ============================================================
    // VENDOR REGISTRATION
    // POST /api/auth/vendor/register
    // ============================================================
    @PostMapping("/vendor/register")
    public ResponseEntity<ApiResponse<AuthResponse>> registerVendor(
            @Valid @RequestBody VendorRegisterRequest request) {
        try {
            log.info("Vendor registration attempt: {}", request.getEmail());

            // In production: check if email exists, save to DB
            // For demo: simulate success
            String hashedPassword = passwordEncoder.encode(request.getPassword());

            // Simulate saved vendor
            Long vendorId = new Random().nextLong(1000, 9999);
            String token = jwtUtil.generateToken(request.getEmail(), "VENDOR", vendorId);
            String refreshToken = jwtUtil.generateRefreshToken(request.getEmail());

            AuthResponse authResponse = AuthResponse.builder()
                    .token(token)
                    .refreshToken(refreshToken)
                    .userId(vendorId)
                    .email(request.getEmail())
                    .fullName(request.getFullName())
                    .businessName(request.getBusinessName())
                    .role("VENDOR")
                    .approvalStatus("PENDING")
                    .profileCompletion(25)
                    .expiresIn(86400L)
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(
                        "Registration successful! Please verify your email.",
                        authResponse));

        } catch (Exception e) {
            log.error("Registration error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Registration failed: " + e.getMessage()));
        }
    }

    // ============================================================
    // VENDOR LOGIN
    // POST /api/auth/vendor/login
    // ============================================================
    @PostMapping("/vendor/login")
    public ResponseEntity<ApiResponse<AuthResponse>> loginVendor(
            @Valid @RequestBody LoginRequest request) {
        try {
            log.info("Vendor login attempt: {}", request.getEmail());

            // In production: fetch from DB and verify password
            // Demo: return mock success
            Long vendorId = 1001L;
            String token = jwtUtil.generateToken(request.getEmail(), "VENDOR", vendorId);
            String refreshToken = jwtUtil.generateRefreshToken(request.getEmail());

            AuthResponse authResponse = AuthResponse.builder()
                    .token(token)
                    .refreshToken(refreshToken)
                    .userId(vendorId)
                    .email(request.getEmail())
                    .fullName("Demo Vendor")
                    .businessName("Demo Business")
                    .role("VENDOR")
                    .approvalStatus("APPROVED")
                    .profileCompletion(75)
                    .expiresIn(86400L)
                    .build();

            return ResponseEntity.ok(ApiResponse.success("Login successful!", authResponse));

        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid credentials"));
        }
    }

    // ============================================================
    // SEND OTP
    // POST /api/auth/otp/send
    // ============================================================
    @PostMapping("/otp/send")
    public ResponseEntity<ApiResponse<Map<String, Object>>> sendOtp(
            @RequestBody OtpRequest request) {
        log.info("OTP send request for: {}", request.getIdentifier());

        // In production: generate and send actual OTP
        // For demo: return success
        Map<String, Object> response = new HashMap<>();
        response.put("message", "OTP sent successfully");
        response.put("expiresIn", 300); // 5 minutes
        response.put("maskedIdentifier", maskIdentifier(request.getIdentifier()));

        return ResponseEntity.ok(ApiResponse.success("OTP sent!", response));
    }

    // ============================================================
    // VERIFY OTP
    // POST /api/auth/otp/verify
    // ============================================================
    @PostMapping("/otp/verify")
    public ResponseEntity<ApiResponse<Map<String, String>>> verifyOtp(
            @RequestBody OtpVerifyRequest request) {
        log.info("OTP verify request for: {}", request.getIdentifier());

        // In production: verify from DB
        // Demo: accept "123456" as valid OTP
        if ("123456".equals(request.getOtpCode())) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "verified");
            response.put("message", "Email verified successfully!");
            return ResponseEntity.ok(ApiResponse.success("OTP verified!", response));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Invalid or expired OTP"));
    }

    // ============================================================
    // FORGOT PASSWORD
    // POST /api/auth/forgot-password
    // ============================================================
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @RequestBody ForgotPasswordRequest request) {
        log.info("Forgot password request for: {}", request.getEmail());

        // In production: send reset link via email
        return ResponseEntity.ok(ApiResponse.success(
            "Password reset link sent to " + maskEmail(request.getEmail()),
            null));
    }

    // ============================================================
    // RESET PASSWORD
    // POST /api/auth/reset-password
    // ============================================================
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        log.info("Password reset attempt");

        // In production: verify token, update password
        return ResponseEntity.ok(ApiResponse.success("Password reset successful!", null));
    }

    // ============================================================
    // REFRESH TOKEN
    // POST /api/auth/refresh
    // ============================================================
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Map<String, String>>> refreshToken(
            @RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (jwtUtil.validateToken(refreshToken)) {
            String email = jwtUtil.extractEmail(refreshToken);
            String newToken = jwtUtil.generateToken(email, "VENDOR", 1001L);

            Map<String, String> response = new HashMap<>();
            response.put("token", newToken);
            response.put("expiresIn", "86400");

            return ResponseEntity.ok(ApiResponse.success("Token refreshed", response));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Invalid refresh token"));
    }

    // ============================================================
    // LOGOUT
    // POST /api/auth/logout
    // ============================================================
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        // In production: invalidate refresh token in DB
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }

    // Helpers
    private String maskIdentifier(String identifier) {
        if (identifier.contains("@")) return maskEmail(identifier);
        if (identifier.length() > 6)
            return identifier.substring(0, 3) + "****" + identifier.substring(identifier.length() - 3);
        return "****";
    }

    private String maskEmail(String email) {
        int atIndex = email.indexOf("@");
        if (atIndex <= 2) return "***" + email.substring(atIndex);
        return email.substring(0, 2) + "***" + email.substring(atIndex);
    }

    // ============================================================
    // DTOs
    // ============================================================
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VendorRegisterRequest {
        @NotBlank(message = "Full name is required")
        @Size(min = 2, max = 150) private String fullName;

        @NotBlank(message = "Business name is required")
        @Size(min = 2, max = 200) private String businessName;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format") private String email;

        @NotBlank(message = "Phone is required")
        @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number") private String phone;

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&]).+$",
                 message = "Password must have uppercase, lowercase, number and special character")
        private String password;

        @NotBlank(message = "Please confirm your password") private String confirmPassword;
        @NotNull(message = "Please select a category") private Long categoryId;
        private String categoryName;
        private Boolean acceptTerms;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        @NotBlank(message = "Email is required")
        @Email private String email;
        @NotBlank(message = "Password is required") private String password;
        private Boolean rememberMe = false;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OtpRequest {
        @NotBlank private String identifier;
        private String type = "EMAIL";
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OtpVerifyRequest {
        @NotBlank private String identifier;
        @NotBlank @Size(min = 6, max = 6) private String otpCode;
        private String type = "EMAIL";
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ForgotPasswordRequest {
        @NotBlank @Email private String email;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResetPasswordRequest {
        @NotBlank private String token;
        @NotBlank @Size(min = 8) private String newPassword;
        @NotBlank private String confirmPassword;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthResponse {
        private String token;
        private String refreshToken;
        private Long userId;
        private String email;
        private String fullName;
        private String businessName;
        private String role;
        private String approvalStatus;
        private Integer profileCompletion;
        private Long expiresIn;
        private String profilePictureUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;
        private LocalDateTime timestamp = LocalDateTime.now();
        private String error;

        public static <T> ApiResponse<T> success(String message, T data) {
            return ApiResponse.<T>builder()
                    .success(true).message(message).data(data)
                    .timestamp(LocalDateTime.now()).build();
        }

        public static <T> ApiResponse<T> error(String error) {
            return ApiResponse.<T>builder()
                    .success(false).error(error)
                    .timestamp(LocalDateTime.now()).build();
        }
    }
}
