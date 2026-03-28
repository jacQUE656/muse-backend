package com.example.musebackend.Response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private String id;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private Role role;
    private String profileImage;

    @JsonProperty("isEmailVerified")
    private boolean isEmailVerified;

    public enum Role{
        USER , ADMIN
    }

}
