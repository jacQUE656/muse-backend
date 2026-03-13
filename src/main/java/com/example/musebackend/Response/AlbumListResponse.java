package com.example.musebackend.Response;

import com.example.musebackend.Models.Album;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlbumListResponse {
    private boolean success;
    @JsonProperty("albums")
    private List<Album> albums;
}
