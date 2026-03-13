package com.example.musebackend.Service;

import com.example.musebackend.Models.Song;
import com.example.musebackend.Repository.SongRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;

@Service
@RequiredArgsConstructor
@Getter
@Setter
public class DownloadService {


    private final SongRepository songRepository;

    public ResponseEntity<Resource> proxyDownload(String songId) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        try {
            // 1. Create a resource from the Cloudinary URL
            URL url = new URL(song.getFile());
            Resource resource = new UrlResource(url);

            // 2. Increment download count in MySQL
            song.setDownloadCount(song.getDownloadCount() + 1);
            songRepository.save(song);

            // 3. Return the stream with a hidden source
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("audio/mpeg"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + song.getName() + ".mp3\"")
                    .body((Resource) resource);

        } catch (IOException e) {
            throw new RuntimeException("Could not process download", e);
        }
    }
}
