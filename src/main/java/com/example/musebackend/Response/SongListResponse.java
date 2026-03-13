package com.example.musebackend.Response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SongListResponse {
    private boolean success;

    @JsonProperty("songs")
    private List<SongResponse> song;
}
