package com.example.musebackend.Service;

import com.example.musebackend.Config.JwtService;
import com.example.musebackend.Exceptions.BusinessException;
import com.example.musebackend.Exceptions.ErrorCode;
import com.example.musebackend.Models.User;
import com.example.musebackend.Models.VerificationToken;
import com.example.musebackend.Repository.UserRepository;
import com.example.musebackend.Repository.VerificationTokenRepository;
import com.example.musebackend.Request.LoginRequest;
import com.example.musebackend.Request.RegisterRequest;
import com.example.musebackend.Response.LoginResponse;
import com.example.musebackend.Response.UserListResponse;
import com.example.musebackend.Response.UserResponse;
import com.example.musebackend.Service.Iservice.IUserService;
import com.example.musebackend.mailing.AccountVerificationEmailContext;
import com.example.musebackend.mailing.EmailService;
import com.example.musebackend.mailing.PasswordResetEmailContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.musebackend.Exceptions.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final VerificationTokenRepository tokenRepository;
    private static BytesKeyGenerator DEFAULT_TOKEN_GENERATOR = KeyGenerators.secureRandom(6);
    private final EmailService emailService;
    @Value("${spring.mail.verify.host.frontend}")
    private String baseUrl;
    @Value("${app.mail.from}")
    private String senderEmail;


    private void checkUserEmail(String email) {
        final boolean exists = this.userRepository.existsByEmailIgnoreCase(email);
        if (exists){
            throw new BusinessException(EMAIL_ALREADY_EXISTS);
        }
    }
    private void checkPhoneNumber(String phoneNumber) {
        final boolean phoneExists = this.userRepository.existsByPhonenumber(phoneNumber);
        if (phoneExists){
            throw new BusinessException(ErrorCode.PHONE_ALREADY_EXISTS);
        }
    }
    public void sendVerifyEMail(User user) {
        // 1. Clean up any existing tokens for this user
        tokenRepository.deleteByUser(user);

        // 2. Generate a fresh 6-character secure token
        String tokenValue = new String(Base64.encodeBase64URLSafe(DEFAULT_TOKEN_GENERATOR.generateKey()));

        // 3. Create and save the VerificationToken entity
        VerificationToken verificationToken = VerificationToken.builder()
                .user(user)
                .token(tokenValue)
                .expireDate(LocalDateTime.now().plusMinutes(20))
                .build();
        tokenRepository.save(verificationToken);

        // 4. Initialize the Email Context
        AccountVerificationEmailContext context = new AccountVerificationEmailContext();
        context.init(user);
        context.setFrom(senderEmail);
        context.setTo(user.getEmail());
        context.setToken(tokenValue);
        context.buildVerificationUrl(baseUrl, verificationToken.getToken());

        try {
            emailService.sendMail(context);
            System.out.println("DEBUG: Email request sent to EmailService for: " + user.getEmail());
        } catch (IOException e) {
            // Detailed logging for Render debugging
            System.err.println("CRITICAL ERROR: Failed to dispatch email to SendGrid for user: " + user.getEmail());
            e.printStackTrace();
            throw new RuntimeException("Registration successful, but verification email failed to send.");
        }
    }
    public void sendPasswordChangeEmail(User user){
        tokenRepository.deleteByUser(user);
        String tokenValue = new String(Base64.encodeBase64URLSafe(DEFAULT_TOKEN_GENERATOR.generateKey()));
        VerificationToken verificationToken = VerificationToken.builder()
                .user(user)
                .token(tokenValue)
                .expireDate(LocalDateTime.now().plusMinutes(10))
                .build();
        tokenRepository.save(verificationToken);
        PasswordResetEmailContext context = new PasswordResetEmailContext();
        context.init(user);
        context.setToken(tokenValue);
        context.setTo(user.getEmail());
        context.setFrom(senderEmail);
        context.buildVerificationUrl(baseUrl , verificationToken.getToken());

        try {
            emailService.sendMail(context);
        }catch (IOException e) {
            // THIS IS THE KEY: We need to see the SendGrid error in your logs
            e.printStackTrace();
            throw new RuntimeException("Email failed to send. Check logs for details: " + e.getMessage());
        }

    }

    @Override
    @Transactional
    public UserResponse registerUser(RegisterRequest request){

        checkUserEmail(request.getEmail());
        checkPhoneNumber(request.getPhonenumber());
         // CREATE NEW USER
      User newUser =  User.builder()
                .firstname(request.getFirstname())
               .lastname(request.getLastname())
                .email(request.getEmail())
                .phonenumber(request.getPhonenumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.USER)
              .emailVerified(false)
                .build();
    userRepository.save(newUser);

sendVerifyEMail(newUser);

        return UserResponse.builder()
            .id(newUser.getId())
            .firstname(newUser.getFirstname())
            .lastname(newUser.getLastname())
            .phone(newUser.getPhonenumber())
            .email(newUser.getEmail())
            .role(UserResponse.Role.USER)
                .isEmailVerified(newUser.isEmailVerified())
            .build();
    }



@Override
public LoginResponse login(LoginRequest request) {
        final Authentication auth = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                        // check if email is verified
                )
        );
        final User user = (User) auth.getPrincipal();
        final String token = this.jwtService.generateAccessToken(user.getUsername());
        final String userId = user.getId();
        final String refreshToken = this.jwtService.generateRefreshToken(user.getUsername());
        final String tokenType = "Bearer";
        return LoginResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .tokenType(tokenType)
                .email(request.getEmail())
                .role(String.valueOf(user.getRole()))
                .id(userId)
                .isEmailVerified(user.isEmailVerified())
                .build();
    }



    @Override
    public User promoteToAdmin(String email){
        User existingUser = userRepository.findByEmail(email);
        existingUser.setRole(User.Role.ADMIN);
        User savedUser = userRepository.save(existingUser);
        return savedUser;
}

@Override
public UserListResponse getAllUser(){

    List<UserResponse> userDto = userRepository.findAll().stream()
            .map(user -> UserResponse.builder()
                    .id(user.getId())
                    .firstname(user.getFirstname())
                    .lastname(user.getLastname())
                    .email(user.getEmail())
                    .phone(user.getPhonenumber())
                    .role(UserResponse.Role.valueOf(String.valueOf(user.getRole())))
                    .build())
            .toList();

    return new UserListResponse(true, userDto);
}

@Override
public Boolean deleteUser(String id){
        User existinguser = userRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("User not found"));
        userRepository.delete(existinguser);
        return true;
    }

    @Transactional
    @Override
    public UserResponse createAdmin(RegisterRequest request){
        checkUserEmail(request.getEmail());

        // CREATE NEW ADMIN
        User newUser =  User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .phonenumber(request.getPhonenumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.ADMIN)
                .emailVerified(true)
                .build();
        userRepository.save(newUser);

        return UserResponse.builder()
                .id(newUser.getId())
                .firstname(newUser.getFirstname())
                .lastname(newUser.getLastname())
                .phone(newUser.getPhonenumber())
                .email(newUser.getEmail())
                .role(UserResponse.Role.ADMIN)
                .build();
    }


    @Override
    public void deactivateAccount(String userId) {
        final User user = userRepository.findById(userId)
                .orElseThrow(()->new BusinessException(USER_NOT_FOUND, userId));
        if (!user.isEnabled()){
            throw new BusinessException(ACCOUNT_ALREADY_DEACTIVATED);
        }

        this.userRepository.save(user);

    }

//    @Override
//    public void reactivateAccount(String userId) {
//        final User user = userRepository.findById(userId)
//                .orElseThrow(()->new BusinessException(USER_NOT_FOUND, userId));
//        if (user.isEnabled()){
//            throw new BusinessException(ACCOUNT_ALREADY_DEACTIVATED);
//        }
//        user.setEnabled(true);
//        this.repo.save(user);
//
//    }
@Transactional
public boolean verifyOTP(String email, String code) {
    // 1. Find the token by the code
    Optional<VerificationToken> tokenOpt = tokenRepository.findByToken(code);

    if (tokenOpt.isPresent()) {
        VerificationToken verificationToken = tokenOpt.get();

        // 2. Check if the token belongs to the right email and isn't expired
        if (verificationToken.getUser().getEmail().equals(email) &&
                verificationToken.getExpireDate().isAfter(LocalDateTime.now())) {

            // 3. Activate the user
            User user = verificationToken.getUser();
            user.setEmailVerified(true);
            userRepository.save(user);

            // 4. Delete the token (no longer needed)
            tokenRepository.delete(verificationToken);
            return true;
        }
    }
    return false;
}

    @Transactional
    public void resendOTP(String email) {
        // 1. Find the user
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Delete any existing tokens to prevent multiple valid tokens
        tokenRepository.deleteByUser(user);
        sendVerifyEMail(user);
    }

    public void VerifyChangePassword(String password, String email , String token) {
//        if (!request.getNewPassword().equals(request.getConfirmNewPassword())){
//            throw new BusinessException(ErrorCode.CHANGE_PASSWORD_MISMATCH);
//        }

        final VerificationToken verificationToken = this.tokenRepository.findByToken(token)
                .orElseThrow(()->new BusinessException(TOKEN_NOT_FOUND, token));

        if (verificationToken.getUser().getEmail().equals(email) &&
                verificationToken.getExpireDate().isAfter(LocalDateTime.now())) {
            User user = verificationToken.getUser();
            final String encoded = this.passwordEncoder.encode(password);
            user.setPassword(encoded);
            this.userRepository.save(user);
            tokenRepository.delete(verificationToken);
        }
    }


    @Transactional
    public User processOAuthPostLogin(String email, String firstName, String lastName) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseGet(() -> {
                    // Create new user if they don't exist
                    User newUser = User.builder()
                            .email(email)
                            .firstname(firstName != null ? firstName : "User")
                            .lastname(lastName != null ? lastName : "")
                            // Google doesn't provide phone, so we use a placeholder or UUID
                            // to satisfy the @Column(nullable = false) constraint
                            .phonenumber("OAUTH_" + java.util.UUID.randomUUID().toString().substring(0, 8))
                            // Password cannot be null, so we set a secure random string
                            // They will never use this password as they login via Google
                            .password(java.util.UUID.randomUUID().toString())
                            .emailVerified(true) // Google emails are pre-verified
                            .role(User.Role.USER)
                            .build();

                    return userRepository.save(newUser);
                });
    }
}
