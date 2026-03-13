package com.example.musebackend.Controller;

import com.example.musebackend.Request.RegisterRequest;
import com.example.musebackend.Response.UserListResponse;
import com.example.musebackend.Response.UserResponse;
import com.example.musebackend.Service.Iservice.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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



}
