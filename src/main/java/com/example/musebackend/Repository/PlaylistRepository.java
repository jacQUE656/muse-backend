package com.example.musebackend.Repository;

import com.example.musebackend.Models.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, String> {
    List<Playlist> findByUserId(String userId);

    Optional<Playlist>findByNameAndUserId(String name , String userId);
}