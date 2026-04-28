package com.library.readspace.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ResourceService {

    private final Path root = Paths.get("uploads/resources");

    public Resource loadFileAsResource(String filename) {
        try {
            Path file = root.resolve(filename).normalize();
            
            // Path traversal protection: ensure the resolved path is still within the root directory
            if (!file.toAbsolutePath().startsWith(root.toAbsolutePath().normalize())) {
                return null;
            }

            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                return null;
            }
        } catch (MalformedURLException e) {
            return null;
        }
    }
}
