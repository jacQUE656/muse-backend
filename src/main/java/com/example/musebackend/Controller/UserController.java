package com.example.musebackend.Controller;

import com.example.musebackend.Models.User;
import com.example.musebackend.Request.RegisterRequest;
import com.example.musebackend.Request.UpdateProfileRequest;
import com.example.musebackend.Response.UserListResponse;
import com.example.musebackend.Response.UserResponse;
import com.example.musebackend.Service.Iservice.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;


    @GetMapping
    public ResponseEntity<?> listUser() {
        try {
            return ResponseEntity.ok(userService.getAllUser());
        } catch (Exception e) {
            return ResponseEntity.ok(new UserListResponse(false, null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @PathVariable String id
    ) {
        try {
            Boolean removed = userService.deleteUser(id);
            if (removed) {
                return ResponseEntity.status(NO_CONTENT).build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PostMapping("/register-admin")
    public ResponseEntity<?> register(
            @Valid
            @RequestBody RegisterRequest request
    ) {

        try {

            UserResponse response = userService.createAdmin(request);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@AuthenticationPrincipal Object principal) {
        try {
            // 1. Check if it's an OAuth2 User
            if (principal instanceof OAuth2User oAuth2User) {
                UserResponse response = UserResponse.builder()
                        .id(oAuth2User.getAttribute("sub")) // Google unique ID
                        .firstname(oAuth2User.getAttribute("given_name"))
                        .lastname(oAuth2User.getAttribute("family_name"))
                        .email(oAuth2User.getAttribute("email"))
                        .profileImage(oAuth2User.getAttribute("picture"))
                        .role(UserResponse.Role.valueOf("LISTENER")) // Default role for OAuth
                        .build();
                return ResponseEntity.ok(response);
            }

            // 2. Fallback to Standard JWT/DB User logic
            UserResponse user = userService.getUserProfile();
            return ResponseEntity.ok(user);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "User not found or session expired");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }


    @PutMapping(value = "/update/profile/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> updateProfile(
            @PathVariable("id") String id,
            @RequestPart("request") String request,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        try {
            // 1. Map the JSON string to your DTO object
            ObjectMapper objectMapper = new ObjectMapper();
            UpdateProfileRequest profileRequest = objectMapper.readValue(request, UpdateProfileRequest.class);

            // 2. Pass the ID and the whole Object to the service
            UserResponse updatedUser = userService.updateProfile(id, profileRequest, file);

            return ResponseEntity.ok(updatedUser);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File processing failed: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Update failed: " + e.getMessage());
        }
    }

    public Map<String, Object> getOauthUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof OAuth2User oAuth2User) {
            // Attributes contains 'name', 'email', 'picture', etc.
            return oAuth2User.getAttributes();
        }

        return Collections.emptyMap();
    }

}
