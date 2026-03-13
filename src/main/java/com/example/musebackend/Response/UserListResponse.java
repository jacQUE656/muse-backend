package com.example.musebackend.Response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserListResponse {

    private boolean success;

    @JsonProperty("user")
    private List<UserResponse> user;
}
