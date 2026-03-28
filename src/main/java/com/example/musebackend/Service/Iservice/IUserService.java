package com.example.musebackend.Service.Iservice;

import com.example.musebackend.Models.User;
import com.example.musebackend.Request.LoginRequest;
import com.example.musebackend.Request.RegisterRequest;
import com.example.musebackend.Request.UpdateProfileRequest;
import com.example.musebackend.Response.LoginResponse;
import com.example.musebackend.Response.UserListResponse;
import com.example.musebackend.Response.UserResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IUserService {

    UserResponse registerUser(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    User promoteToAdmin(String email);

    UserListResponse getAllUser();

    Boolean deleteUser(String id);

    @Transactional
    UserResponse createAdmin(RegisterRequest request);

    void deactivateAccount(String userId);
    UserResponse getUserProfile();

    UserResponse updateProfile(String id , UpdateProfileRequest request, MultipartFile file) throws IOException;
}