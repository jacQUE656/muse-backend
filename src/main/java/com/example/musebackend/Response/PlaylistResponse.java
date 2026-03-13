package com.example.musebackend.Response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PlaylistResponse {
    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("ownerName")
    private String ownerName;
    @JsonProperty("description")
    private String description;
    @JsonProperty("songs")
    private List<SongResponse> song;

}
