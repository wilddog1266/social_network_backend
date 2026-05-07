package com.example.media_service.controller;

import com.example.media_service.entity.enums.MediaType;
import com.example.media_service.response.MediaResponse;
import com.example.media_service.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PostMapping(value = "/upload", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MediaResponse> upload(@RequestParam(name = "file") MultipartFile file,
                                               @RequestParam(name = "type") MediaType type) {
        return ResponseEntity.status(201).body(mediaService.upload(file, type));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MediaResponse> getMediaById(@PathVariable Long id) {
        return ResponseEntity.ok(mediaService.getById(id));
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<MediaResponse> getPublicMediaById(@PathVariable Long id) {
        return ResponseEntity.ok(mediaService.getPublicById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMediaById(@PathVariable Long id) {
        mediaService.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
