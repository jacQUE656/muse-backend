package com.example.musebackend.Controller;

import com.example.musebackend.Request.PlaylistRequest;
import com.example.musebackend.Response.PlaylistResponse;
import com.example.musebackend.Service.MyPlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlaylistController {
    private final MyPlaylistService playlistService;

    @PostMapping
    public ResponseEntity<?> createPlaylist(
            @RequestBody PlaylistRequest request
            ){

        try {

            PlaylistResponse response = playlistService.createPlaylist(request);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<?> addSongToPlayList(
            @PathVariable String playlistId,
            @PathVariable String songId
    ){
        try{
            PlaylistResponse response = playlistService.addSongToPlaylist(playlistId , songId);
            return ResponseEntity.ok(response);

    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
        catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserPlaylist(@PathVariable String userId){
       try{
           return ResponseEntity.ok(playlistService.getAllUserPlayList(userId));
       } catch (RuntimeException e) {
           return ResponseEntity.badRequest().body(e.getMessage());
       } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
       }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeAlbum(
            @PathVariable String id
    ){
        try {
            Boolean removed = playlistService.deletePlaylist(id);
            if (removed){
                return ResponseEntity.status(NO_CONTENT).build();
            }else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{playlistId}/song/{songId}")
    public ResponseEntity<?> removeSong(@PathVariable String playlistId,@PathVariable String songId){
        try {

                return ResponseEntity.ok(playlistService.removeSongFromPlaylist(playlistId , songId));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPlaylistById(@PathVariable String id) {
        PlaylistResponse playlistResponse = playlistService.getPlaylistById(id);
        return ResponseEntity.ok(playlistResponse);

    }

    @PutMapping("/{id}/rename")
    public ResponseEntity<PlaylistResponse> renamePlaylist(
            @PathVariable String id,
            @RequestBody Map<String , String> updates
            ){
        String newName = updates.get("name");
        PlaylistResponse updatedPlaylist = playlistService.renamePlaylist(id,newName);
        return ResponseEntity.ok(updatedPlaylist);
    }

}
