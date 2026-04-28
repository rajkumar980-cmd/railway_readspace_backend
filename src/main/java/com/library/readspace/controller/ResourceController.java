package com.library.readspace.controller;

import com.library.readspace.model.Resource;
import com.library.readspace.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import com.library.readspace.service.ResourceService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private ResourceService resourceService;

    private final String uploadDir = "uploads/resources";

    @GetMapping
    public List<Resource> getAllResources() {
        return resourceRepository.findAll();
    }

    @GetMapping("/view/{filename:.+}")
    public ResponseEntity<org.springframework.core.io.Resource> viewFile(@PathVariable String filename) {
        org.springframework.core.io.Resource file = resourceService.loadFileAsResource(filename);
        if (file == null) {
            return ResponseEntity.notFound().build();
        }

        String contentType = "application/octet-stream";
        if (filename.toLowerCase().endsWith(".pdf")) {
            contentType = "application/pdf";
        } else if (filename.toLowerCase().endsWith(".epub")) {
            contentType = "application/epub+zip";
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(file);
    }

    @PostMapping
    public Resource createResource(
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("category") String category,
            @RequestParam("categoryLabel") String categoryLabel,
            @RequestParam("description") String description,
            @RequestParam(value = "thumbnail", required = false) String thumbnail,
            @RequestParam("pages") Integer pages,
            @RequestParam("year") Integer year,
            @RequestParam("tags") String tags,
            @RequestParam("fileSize") String fileSize,
            @RequestParam(value = "externalUrl", required = false) String externalUrl,
            @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {

        try {
            Resource resource = new Resource();
            resource.setTitle(title);
            resource.setAuthor(author);
            resource.setCategory(category);
            resource.setCategoryLabel(categoryLabel);
            resource.setDescription(description);
            
            if (thumbnail == null || thumbnail.isEmpty()) {
                resource.setThumbnail("https://images.unsplash.com/photo-1543005127-837384a6b36e?auto=format&fit=crop&q=80&w=300");
            } else {
                resource.setThumbnail(thumbnail);
            }

            resource.setPages(pages);
            resource.setYear(year);
            
            if (tags != null && !tags.isEmpty()) {
                resource.setTags(java.util.Arrays.asList(tags.split(",")));
            } else {
                resource.setTags(new java.util.ArrayList<>());
            }

            resource.setFileSize(fileSize);
            resource.setExternalUrl(externalUrl);
            resource.setRating(0.0);
            resource.setDownloads(0);
            resource.setFeatured(true);
            resource.setLatest(true);

            if (file != null && !file.isEmpty()) {
                // Validation: PDF and EPUB only
                String contentType = file.getContentType();
                String originalFileName = file.getOriginalFilename();
                boolean isValidType = (contentType != null && (contentType.equals("application/pdf") || contentType.equals("application/epub+zip")));
                
                if (!isValidType && originalFileName != null) {
                    String lowerName = originalFileName.toLowerCase();
                    isValidType = lowerName.endsWith(".pdf") || lowerName.endsWith(".epub");
                }
                
                if (!isValidType) {
                    throw new IllegalArgumentException("Invalid file type. Only PDF and EPUB are allowed.");
                }

                // Validation: 25MB Max
                if (file.getSize() > 25 * 1024 * 1024) {
                    throw new IllegalArgumentException("File size exceeds 25MB limit.");
                }

                Path path = Paths.get(uploadDir);
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                }
                
                String storedFileName = UUID.randomUUID().toString() + "_" + originalFileName;
                Path filePath = path.resolve(storedFileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                
                resource.setFileUrl("/uploads/resources/" + storedFileName);
                resource.setFileName(originalFileName);
                resource.setFilePath(filePath.toString());
                
                // Calculate and set file size from actual file
                double sizeInMb = file.getSize() / (1024.0 * 1024.0);
                if (sizeInMb < 0.1) {
                    resource.setFileSize(String.format("%.1f KB", file.getSize() / 1024.0));
                } else {
                    resource.setFileSize(String.format("%.1f MB", sizeInMb));
                }
            } else {
                resource.setFileSize(fileSize);
            }

            return resourceRepository.save(resource);
        } catch (Exception e) {
            System.err.println("Error creating resource: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        System.out.println("DEBUG: Attempting to delete resource with ID: " + id);
        if (resourceRepository.existsById(id)) {
            resourceRepository.deleteById(id);
            System.out.println("DEBUG: Successfully deleted resource ID: " + id);
            return ResponseEntity.ok().build();
        }
        System.out.println("DEBUG: Resource not found for deletion, ID: " + id);
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<Resource> updateResource(@PathVariable Long id, @RequestBody Resource resourceDetails) {
        System.out.println("DEBUG: Attempting to update resource with ID: " + id);
        return resourceRepository.findById(id).map(resource -> {
            // Update only fields that are provided or intended to be editable
            if (resourceDetails.getTitle() != null) resource.setTitle(resourceDetails.getTitle());
            if (resourceDetails.getAuthor() != null) resource.setAuthor(resourceDetails.getAuthor());
            if (resourceDetails.getCategory() != null) resource.setCategory(resourceDetails.getCategory());
            if (resourceDetails.getCategoryLabel() != null) resource.setCategoryLabel(resourceDetails.getCategoryLabel());
            if (resourceDetails.getDescription() != null) resource.setDescription(resourceDetails.getDescription());
            if (resourceDetails.getThumbnail() != null) resource.setThumbnail(resourceDetails.getThumbnail());
            if (resourceDetails.getPages() != null) resource.setPages(resourceDetails.getPages());
            if (resourceDetails.getYear() != null) resource.setYear(resourceDetails.getYear());
            if (resourceDetails.getTags() != null) resource.setTags(resourceDetails.getTags());
            if (resourceDetails.getFeatured() != null) resource.setFeatured(resourceDetails.getFeatured());
            if (resourceDetails.getLatest() != null) resource.setLatest(resourceDetails.getLatest());
            if (resourceDetails.getExternalUrl() != null) resource.setExternalUrl(resourceDetails.getExternalUrl());
            
            // preserved fields (only update if explicitly sent and not null)
            if (resourceDetails.getFileSize() != null) resource.setFileSize(resourceDetails.getFileSize());
            if (resourceDetails.getFileUrl() != null) resource.setFileUrl(resourceDetails.getFileUrl());
            if (resourceDetails.getRating() != null) resource.setRating(resourceDetails.getRating());
            if (resourceDetails.getDownloads() != null) resource.setDownloads(resourceDetails.getDownloads());

            Resource saved = resourceRepository.save(resource);
            System.out.println("DEBUG: Successfully updated resource ID: " + id + " in database.");
            return ResponseEntity.ok(saved);
        }).orElseGet(() -> {
            System.out.println("DEBUG: Resource not found for update, ID: " + id);
            return ResponseEntity.notFound().build();
        });
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Resource> updateResourceStatus(@PathVariable Long id, @RequestBody java.util.Map<String, Object> payload) {
        System.out.println("DEBUG: Attempting to toggle status for resource ID: " + id);
        return resourceRepository.findById(id).map(resource -> {
            if (payload.containsKey("featured")) {
                resource.setFeatured((Boolean) payload.get("featured"));
            }
            if (payload.containsKey("latest")) {
                resource.setLatest((Boolean) payload.get("latest"));
            }
            return ResponseEntity.ok(resourceRepository.save(resource));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/download")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<Void> recordDownload(@PathVariable Long id) {
        return resourceRepository.findById(id).map(resource -> {
            int current = resource.getDownloads() != null ? resource.getDownloads() : 0;
            resource.setDownloads(current + 1);
            resourceRepository.save(resource);
            System.out.println("DEBUG: Download count incremented for resource ID: " + id);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
