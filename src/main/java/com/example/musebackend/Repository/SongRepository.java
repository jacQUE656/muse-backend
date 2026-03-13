package com.example.musebackend.Repository;

import com.example.musebackend.Models.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository extends JpaRepository<Song , String> {
}
