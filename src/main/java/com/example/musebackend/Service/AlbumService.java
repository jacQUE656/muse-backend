package com.example.musebackend.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.musebackend.Models.Album;
import com.example.musebackend.Repository.AlbumRepository;
import com.example.musebackend.Request.AlbumRequest;
import com.example.musebackend.Response.AlbumListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final Cloudinary cloudinary;



    public Album addAlbum(AlbumRequest request) throws IOException {

       Map<String, Object> imageUploadResult = cloudinary.uploader()
               .upload(request.getImageFile().getBytes() ,
                       ObjectUtils
                               .asMap("resource_type", "image"));
       Album newAlbum =  Album.builder()
               .name(request.getName())
               .description(request.getDescription())
               .bgColor(request.getBgColor())
               .imageUrl(imageUploadResult.get("secure_url").toString())
               .build();
      return albumRepository.save(newAlbum);
    }

    public AlbumListResponse getAllAlbums(){

        return new AlbumListResponse(true , albumRepository.findAll());
    }


    public Boolean removeAlbum(String id){
      Album existingAlbum =   albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Album not found"));
        albumRepository.delete(existingAlbum);
        return true;
    }
    public AlbumListResponse getAlbumById(String id){
        Album album = albumRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Album not found"));
        return new AlbumListResponse(true , Collections.singletonList(album));

    }
}
