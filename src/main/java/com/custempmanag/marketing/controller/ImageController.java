package com.custempmanag.marketing.controller;

import com.custempmanag.marketing.config.UserPrinciple;
import com.custempmanag.marketing.repository.ImageRepository;
import com.custempmanag.marketing.response.MessageResponse;
import com.custempmanag.marketing.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@PreAuthorize("hasPermission(null, null, 'EV')")
public class ImageController {

    private final ImageRepository imageRepository;
    private final ImageService imageService;
    @Value("${file.upload-dir}")
    private String fileUploadDir;

    @GetMapping("/files/{folder}/{filename}")
    public ResponseEntity<Resource> getFile(
            @PathVariable String folder,
            @PathVariable String filename) throws IOException {

        Path file = Paths.get(fileUploadDir, folder, filename);
        if (!Files.exists(file)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(file.toUri());
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }


    @DeleteMapping("/images/{imageId}")
    @PreAuthorize("hasPermission(#imageId, 'Image', 'D0') or hasPermission(#imageId, 'Image', 'D1')")
    public ResponseEntity<MessageResponse> deleteFile(
            @PathVariable Long imageId) {

        MessageResponse messageResponse = imageService.deleteImage(imageId);
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }


}
