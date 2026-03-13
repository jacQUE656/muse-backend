package com.example.musebackend.Request;

import lombok.Data;

@Data
public class PlaylistRequest {
    private  String name;
    private String description;
    private String userId;

}
