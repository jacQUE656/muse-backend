package com.example.musebackend.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.musebackend.Models.Song;
import com.example.musebackend.Repository.SongRepository;
import com.example.musebackend.Request.SongRequest;
import com.example.musebackend.Response.SongListResponse;
import com.example.musebackend.Response.SongResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SongService {

    private final SongRepository songRepository;
    private final Cloudinary cloudinary;

    private String formatDuration(Double durationSeconds) {
        if (durationSeconds == null){
            return "0.00";
        }
        int minutes = (int)(durationSeconds / 60);
        int seconds = (int)(durationSeconds % 60);
        return String.format("%d:%02d", minutes , seconds);
    }
    public Song addSong(SongRequest request) throws IOException {
       Map<String ,Object> audioUploadResult = cloudinary.uploader()
                .upload(request.getAudioFile()
                        .getBytes() ,
                        ObjectUtils.
                                asMap("resource_type", "video"));

        Map<String ,Object> imageUploadResult = cloudinary.uploader()
                .upload(request.getImageFile()
                                .getBytes() ,
                        ObjectUtils.
                                asMap("resource_type", "image"));
       Double durationSeconds  = (Double)audioUploadResult.get("duration");
       String duration = formatDuration(durationSeconds);

   Song newSong =   Song.builder()
               .name(request.getName())
               .description(request.getDescription())
               .album(request.getAlbum())
               .image(imageUploadResult.get("secure_url").toString())
               .file(audioUploadResult.get("secure_url").toString())
               .duration(duration)
           .downloadCount(0)
           .dateAdded(LocalDate.now())
               .build();
          return  songRepository.save(newSong);
    }


 public SongListResponse getAllSongs() {
    List<SongResponse> songDtos = songRepository.findAll().stream()
            .map(song -> SongResponse.builder()
                    .id(song.getId())
                    .name(song.getName())
                    .album(song.getAlbum())
                    .description(song.getDescription())
                    .duration(song.getDuration())
                    .image(song.getImage())
                    .file(song.getFile())
                    .dateAdded(song.getDateAdded())
                    .build())
            .toList();

    return new SongListResponse(true, songDtos);
}

    public SongListResponse getSongById(String id) {
        SongResponse songDto = songRepository.findById(id)
                .map(song -> SongResponse.builder()
                        .id(song.getId())
                        .name(song.getName())
                        .album(song.getAlbum())
                        .description(song.getDescription())
                        .duration(song.getDuration())
                        .image(song.getImage())
                        .file(song.getFile())
                        .dateAdded(song.getDateAdded())
                        .build())
                .orElseThrow(() -> new RuntimeException("Song not found with id: " + id));

        return new SongListResponse(true, Collections.singletonList(songDto));
    }

    public Boolean removeSong(String id){
        Song existingSong = songRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Song not found"));
        songRepository.delete(existingSong);
        return true;
    }

}
