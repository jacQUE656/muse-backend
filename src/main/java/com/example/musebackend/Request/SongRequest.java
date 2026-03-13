package com.example.musebackend.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SongRequest {

    private String id;

    private String name;

    private String description;

    private String album;

    private MultipartFile audioFile;

    private MultipartFile imageFile;

}
