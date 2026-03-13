package com.example.musebackend.Service;

import com.example.musebackend.Models.Playlist;
import com.example.musebackend.Models.Song;
import com.example.musebackend.Models.User;
import com.example.musebackend.Repository.PlaylistRepository;
import com.example.musebackend.Repository.SongRepository;
import com.example.musebackend.Repository.UserRepository;
import com.example.musebackend.Request.PlaylistRequest;
import com.example.musebackend.Response.PlaylistResponse;
import com.example.musebackend.Response.SongListResponse;
import com.example.musebackend.Response.SongResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPlaylistService {


    private final PlaylistRepository playlistRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;

    public PlaylistResponse createPlaylist(PlaylistRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(()-> new RuntimeException("User not found"));
        playlistRepository.findByNameAndUserId(request.getName(), request.getUserId()).ifPresent
                (existing -> { throw new RuntimeException("Playlist '" + request.getName() + "'already exists for this user");

                });

        Playlist playlist = Playlist.builder()
                .name(request.getName())
                .user(user)
                .description(request.getDescription())
                .build();




        playlistRepository.save(playlist);
        return PlaylistResponse.builder()
                .id(playlist.getId())
                .name(playlist.getName())
                .description(playlist.getDescription())
                .ownerName(playlist.getUser().getEmail())
                .build();
    }

    public Boolean deletePlaylist(String id) {
        playlistRepository.deleteById(id);
        return true;
    }

  @Transactional
    public PlaylistResponse addSongToPlaylist(String playlistId, String songId) {
        // 1. Fetch Entities
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        // 2. Update Relationship
        if (!playlist.getSongs().contains(song)) {
            playlist.getSongs().add(song);
        }

        // 3. Save
        Playlist savedPlaylist = playlistRepository.save(playlist);

        // 4. Map to DTO before returning to stop the circular reference
        return PlaylistResponse.builder()
                .id(savedPlaylist.getId())
                .name(savedPlaylist.getName())
                .ownerName(savedPlaylist.getUser().getEmail())
                .song(SongListResponse.builder()
                        .success(true)
                        .song(savedPlaylist.getSongs().stream()
                                .map(s -> SongResponse.builder()
                                        .id(s.getId())
                                        .name(s.getName())
                                        .album(s.getAlbum())
                                        .duration(s.getDuration())
                                        .file(s.getFile())
                                        .image(s.getImage())
                                        .build())
                                .toList())
                        .build().getSong())
                .build();

    }

    public List<PlaylistResponse> getAllUserPlayList(String userId){
        List<Playlist> playlists = playlistRepository.findByUserId(userId);

        return playlists.stream()
                .map(playlist -> PlaylistResponse.builder()
                        .id(playlist.getId())
                        .name(playlist.getName())
                        // If your Entity uses a User object, use playlist.getUser().getUsername()
                        .ownerName(playlist.getUser().getEmail())
                        .song(SongListResponse.builder()
                                .success(true)
                                .song(playlist.getSongs().stream() // 1. Stream the Song entities
                                        .map(songEntity -> SongResponse.builder() // 2. Map to SongResponse DTO
                                                .id(songEntity.getId())
                                                .name(songEntity.getName())
                                                .album(songEntity.getAlbum())
                                                .description(songEntity.getDescription())
                                                .duration(songEntity.getDuration())
                                                .build())
                                        .toList()) // 3. Collect into List<SongResponse>
                                .build().getSong()) // 4. Build the SongListResponse
                        .build()) // 5. Build the PlaylistResponse
                .toList(); // 6. Collect into List<PlaylistResponse>
    }

    @Transactional
    public PlaylistResponse removeSongFromPlaylist(String playlistId, String songId) {
        // 1. Find the playlist
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        // 2. Find the song
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        // 3. Remove the song from the list
        // This works automatically if your Song entity has a proper equals() method
        playlist.getSongs().remove(song);

        // 4. Save the changes
        Playlist updatedPlaylist = playlistRepository.save(playlist);
        return PlaylistResponse.builder()
                .id(updatedPlaylist.getId())
                .name(updatedPlaylist.getName())
                .ownerName(updatedPlaylist.getUser().getEmail())
                .song(SongListResponse.builder()
                        .success(true)
                        .song(updatedPlaylist.getSongs().stream()
                                .map(s -> SongResponse.builder()
                                        .id(s.getId())
                                        .name(s.getName())
                                        .album(s.getAlbum())
                                        .duration(s.getDuration())
                                        .file(s.getFile())
                                        .image(s.getImage())
                                        .build())
                                .toList())
                        .build().getSong())
                .build();

    }

    public PlaylistResponse getPlaylistById(String id){
        Playlist playlist = playlistRepository.findById(id).
                orElseThrow(()-> new RuntimeException("Playlist not found with id:" + id));
        return  PlaylistResponse.builder()
                .id(playlist.getId())
                .name(playlist.getName())
                .ownerName(playlist.getUser().getEmail())
                .song(SongListResponse.builder()
                        .success(true)
                        .song(playlist.getSongs().stream()
                                .map(s -> SongResponse.builder()
                                        .id(s.getId())
                                        .name(s.getName())
                                        .album(s.getAlbum())
                                        .duration(s.getDuration())
                                        .file(s.getFile())
                                        .image(s.getImage())
                                        .build())
                                .toList())
                        .build().getSong())
                .build();
    }

    public PlaylistResponse renamePlaylist (String id, String newName){
        Playlist splaylist = playlistRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Playlist not found"));
        splaylist.setName(newName);
       Playlist playlist = playlistRepository.save(splaylist);
        return  PlaylistResponse.builder()
                .id(playlist.getId())
                .name(playlist.getName())
                .ownerName(playlist.getUser().getEmail())
                .song(SongListResponse.builder()
                        .success(true)
                        .song(playlist.getSongs().stream()
                                .map(s -> SongResponse.builder()
                                        .id(s.getId())
                                        .name(s.getName())
                                        .album(s.getAlbum())
                                        .duration(s.getDuration())
                                        .file(s.getFile())
                                        .image(s.getImage())
                                        .build())
                                .toList())
                        .build().getSong())
                .build();

    }

}
