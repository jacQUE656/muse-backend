package com.example.musebackend.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    @Setter
    @Table(name = "playlists")
    public class Playlist {

        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        @JsonProperty("id")
        private String id;

        private String name;
        private String description;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        private User user; // The owner

        @ManyToMany(fetch = FetchType.LAZY)
        @JoinTable(
                name = "playlist_songs",
                joinColumns = @JoinColumn(name = "playlist_id"),
                inverseJoinColumns = @JoinColumn(name = "song_id")
        )
        @Builder.Default
        private List<Song> songs=new ArrayList<>();



        // Getters and Setters...

}
