package com.example.musebackend.Repository;

import com.example.musebackend.Models.Album;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumRepository extends JpaRepository<Album , String> {
}
