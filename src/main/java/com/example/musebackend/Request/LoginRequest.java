package com.example.musebackend.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {
    @NotBlank(message = "VALIDATION.AUTHENTICATION.EMAIL.NOT_BLANK")
    @Email(message = "VALIDATION.AUTHENTICATION.EMAIL.NOT_FORMAT")
    private String email;

    @NotBlank(message = "VALIDATION.AUTHENTICATION.PASSWORD.NOT_BLANK")
    private String password;

    private String portal;
}
