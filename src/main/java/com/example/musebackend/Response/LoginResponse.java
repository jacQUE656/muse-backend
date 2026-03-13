package com.example.musebackend.Response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
    @JsonProperty("token")
    private String accessToken;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("user")
    private String email;
    @JsonProperty("user_role")
    private String role;
    @JsonProperty("user_id")
    private String id;

    @JsonProperty("isEmailVerified")
    private boolean isEmailVerified;

}
