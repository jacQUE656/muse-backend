package com.example.musebackend.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name = "albums")
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JsonProperty("id")
    private String id;

    private String name;

    private String description;

    private String bgColor;

    private String imageUrl;
}
