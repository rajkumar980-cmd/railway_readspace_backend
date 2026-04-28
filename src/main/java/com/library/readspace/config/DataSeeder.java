package com.library.readspace.config;

import com.library.readspace.model.Resource;
import com.library.readspace.model.User;
import com.library.readspace.repository.ResourceRepository;
import com.library.readspace.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner loadData(ResourceRepository resourceRepository, UserRepository userRepository) {
        return args -> {
            // Fix existing high download numbers from previous mock data
            resourceRepository.findAll().forEach(r -> {
                if (r.getDownloads() != null && r.getDownloads() > 1000) {
                    r.setDownloads(new java.util.Random().nextInt(40) + 10);
                    resourceRepository.save(r);
                }
            });

            if (userRepository.count() == 0) {
                User admin = new User();
                admin.setName("Admin User");
                admin.setEmail("admin@example.com");
                admin.setPassword("admin");
                admin.setRole("admin");
                admin.setJoined(LocalDate.now());
                admin.setDownloads(0);
                admin.setStatus("active");
                
                User student = new User();
                student.setName("Demo Student");
                student.setEmail("student@example.com");
                student.setPassword("student");
                student.setRole("student");
                student.setJoined(LocalDate.now());
                student.setDownloads(0);
                student.setStatus("active");
                
                userRepository.saveAll(List.of(admin, student));
            }
            if (resourceRepository.count() == 0) {
                Resource r1 = new Resource();
                r1.setTitle("Introduction to Algorithms (4th Edition)");
                r1.setAuthor("Cormen, Leiserson, Rivest & Stein");
                r1.setCategory("textbooks");
                r1.setCategoryLabel("Textbook");
                r1.setDescription("A comprehensive introduction to modern algorithms...");
                r1.setThumbnail("https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400&h=280&fit=crop&auto=format");
                r1.setPages(1292);
                r1.setYear(2022);
                r1.setRating(4.9);
                r1.setDownloads(45);
                r1.setTags(List.of("algorithms", "data structures", "computer science"));
                r1.setFeatured(true);
                r1.setLatest(false);
                r1.setFileSize("18.4 MB");

                Resource r2 = new Resource();
                r2.setTitle("Deep Learning with Python");
                r2.setAuthor("François Chollet");
                r2.setCategory("textbooks");
                r2.setCategoryLabel("Textbook");
                r2.setDescription("A practical guide to deep learning using Keras...");
                r2.setThumbnail("https://images.unsplash.com/photo-1555949963-aa79dcee981c?w=400&h=280&fit=crop&auto=format");
                r2.setPages(504);
                r2.setYear(2021);
                r2.setRating(4.8);
                r2.setDownloads(32);
                r2.setTags(List.of("deep learning", "python", "keras", "AI"));
                r2.setFeatured(true);
                r2.setLatest(false);
                r2.setFileSize("12.1 MB");

                resourceRepository.saveAll(List.of(r1, r2));
            }
        };
    }
}
