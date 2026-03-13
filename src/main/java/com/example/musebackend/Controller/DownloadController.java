package com.example.musebackend.Controller;

import com.example.musebackend.Service.DownloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/music")
@RequiredArgsConstructor
public class DownloadController {


    private final DownloadService downloadService;

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable String id) {
        return downloadService.proxyDownload(id);
    }
}