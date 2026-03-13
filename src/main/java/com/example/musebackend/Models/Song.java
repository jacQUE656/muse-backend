package com.example.musebackend.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "songs")
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JsonProperty("id")
    private String id;

    private String name;

    private String description;

    private String album;

    private String image;

    private String file;

    private String duration;

    private int downloadCount;

    @ManyToMany
    @Builder.Default
    private Set<Playlist> playlists = new HashSet<>();
}
