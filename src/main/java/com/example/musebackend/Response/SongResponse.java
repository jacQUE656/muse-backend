package com.example.musebackend.Response;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SongResponse {
    private String id;

    private String name;

    private String description;

    private String album;

    private String image;

    private String file;

    private String duration;
}
