package com.example.musebackend.Controller;

import com.example.musebackend.Config.JwtService;
import com.example.musebackend.Exceptions.BusinessException;
import com.example.musebackend.Exceptions.ErrorCode;
import com.example.musebackend.Models.User;
import com.example.musebackend.Models.VerificationToken;
import com.example.musebackend.Repository.UserRepository;
import com.example.musebackend.Repository.VerificationTokenRepository;
import com.example.musebackend.Request.LoginRequest;
import com.example.musebackend.Request.OtpRequest;
import com.example.musebackend.Request.RegisterRequest;
import com.example.musebackend.Response.UserResponse;
import com.example.musebackend.Service.UserService;
import com.example.musebackend.mailing.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
  private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid
            @RequestBody
            final LoginRequest request
            ){
        User eUser = userRepository.findByEmail(request.getEmail());
        if (request.getPortal().equalsIgnoreCase("admin") &&
                eUser.getRole().name().equalsIgnoreCase("USER")){
            return ResponseEntity.badRequest().body("Admin access required");
        }
        if (!eUser.isEmailVerified()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Account not verified. Please verify your email.");
        }

      try{
          return ResponseEntity.ok(this.userService.login(request));
      } catch (BadCredentialsException e) {
          return ResponseEntity.badRequest().body("Email / Password Incorrect");
      }

      catch (RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
        catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid
            @RequestBody RegisterRequest request
            ){

        try {

            UserResponse response = userService.registerUser(request);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @PostMapping("/resend-code")
    public ResponseEntity<?> resendCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        User user = userRepository.findByEmailIgnoreCase(email).orElseThrow();

        // 1. Delete old token if it exists
        tokenRepository.deleteByUser(user);

        // 2. Generate and save new 6-digit OTP
        String newOtp = String.format("%06d", new Random().nextInt(999999));
        VerificationToken newToken = VerificationToken.builder()
                .user(user)
                .token(newOtp)
                .build();
        tokenRepository.save(newToken);

        // 3. Send email asynchronously
       // emailService.sendMail(email, newOtp);
        userService.sendVerifyEMail(user);

        return ResponseEntity.ok("New code sent.");
    }


    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam String email, @RequestParam String token) {
        boolean isVerified = userService.verifyOTP(email , token);

        if (isVerified) {
            return ResponseEntity.ok("User verified successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid or expired OTP.");
        }
    }
    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtpManual(@RequestBody OtpRequest request) {
        // You can also add basic validation here
        if (request.getToken() == null || request.getToken().isEmpty()) {
            return ResponseEntity.badRequest().body("Token is required");
        }

        boolean isVerified = userService.verifyOTP(request.getEmail(), request.getToken());

        if (isVerified) {
            return ResponseEntity.ok("Verified successfully!");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid or expired code.");
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<String> resendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required.");
        }

        try {
            userService.resendOTP(email);
            return ResponseEntity.ok("New OTP has been sent to your email.");
        } catch (Exception e) {
            // Return a generic error to avoid exposing if the user exists
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while resending the OTP.");
        }
    }
    @PostMapping("/verify/reset-password")
    public ResponseEntity<String> verifyChangePassword(
            @RequestBody String request,
            @RequestParam String email,
            @RequestParam String token) {

        userService.VerifyChangePassword(request, email, token);

        return ResponseEntity.ok("Password has been successfully updated.");
    }
    @PostMapping("/reset-password")
    public ResponseEntity<String> forgotPassword(@RequestBody String email) {
        // 1. Validate if user exists
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        // 2. Trigger the password reset email logic
        userService.sendPasswordChangeEmail(user);

        return ResponseEntity.ok("If an account exists for this email, a reset link has been sent.");
    }


    @GetMapping("/oauth2/success")
    public ResponseEntity<?> googleLoginSuccess(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("Authentication failed");
        }

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String firstName = oAuth2User.getAttribute("given_name");
        String lastName = oAuth2User.getAttribute("family_name");

        // 1. Process the user (Find or Create)
        User user = userService.processOAuthPostLogin(email, firstName, lastName);

        // 2. Generate Muse Tokens
        String accessToken = jwtService.generateAccessToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        // 3. Return the tokens and user info
        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken,
                "user", Map.of(
                            "id", user.getId(),
                        "email", user.getEmail(),
                        "firstName", user.getFirstname(),
                        "role", user.getRole().name(),
                        "isEmailVerified", user.isEnabled()
                )
        ));
    }


}
