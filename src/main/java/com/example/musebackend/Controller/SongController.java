package com.example.musebackend.Controller;

import com.example.musebackend.Request.SongRequest;
import com.example.musebackend.Response.AlbumListResponse;
import com.example.musebackend.Service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/songs")
@RequiredArgsConstructor

public class SongController {
    private final SongService songService;

    @PostMapping
    public ResponseEntity<?> addSong(
            @RequestPart("request") String requestString,
            @RequestPart("audio") MultipartFile audioFile,
            @RequestPart("image") MultipartFile imageFile
    ) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            SongRequest songRequest = objectMapper.readValue(requestString , SongRequest.class);
            songRequest.setImageFile(imageFile);
            songRequest.setAudioFile(audioFile);
            return ResponseEntity.status(CREATED)
                    .body( songService.addSong(songRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> listSongs(){
        try {
            return ResponseEntity.ok(songService.getAllSongs());
        } catch (Exception e) {
            return ResponseEntity.ok(new AlbumListResponse(false, null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeSongs(
            @PathVariable String id
    ){
        try {
            Boolean removed = songService.removeSong(id);
            if (removed){
                return ResponseEntity.status(NO_CONTENT).build();
            }else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getSongById(
            @PathVariable String id
    ){
        try {
            return ResponseEntity.ok(songService.getSongById(id));
        } catch (Exception e) {
            return ResponseEntity.ok(new AlbumListResponse(false, null));
        }
    }
}
