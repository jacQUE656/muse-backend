package com.example.musebackend.Controller;

import com.example.musebackend.Request.AlbumRequest;
import com.example.musebackend.Response.AlbumListResponse;
import com.example.musebackend.Service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {

    @Autowired
    private AlbumService albumService;

    @PostMapping
    public ResponseEntity<?> addAlbum(
            @RequestPart("request") String request,
            @RequestPart("file")MultipartFile file
            ){

        try {
            ObjectMapper objectMapper = new ObjectMapper();
           AlbumRequest albumRequest = objectMapper.readValue(request, AlbumRequest.class);
            albumRequest.setImageFile(file);

            return ResponseEntity.status(CREATED).body(albumService.addAlbum(albumRequest));

        }catch (Exception e){
        return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> listAlbums(){
        try {
            return ResponseEntity.ok(albumService.getAllAlbums());
        } catch (Exception e) {
            return ResponseEntity.ok(new AlbumListResponse(false, null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeAlbum(
            @PathVariable String id
    ){
try {
   Boolean removed = albumService.removeAlbum(id);
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
    public ResponseEntity<AlbumListResponse> getAlbumById(
            @PathVariable String id
    ){
        try {
            return ResponseEntity.ok(albumService.getAlbumById(id));
        } catch (Exception e) {
            return ResponseEntity.ok(new AlbumListResponse(false, null));
        }
    }
}
