package com.library.readspace.controller;

import com.library.readspace.model.Review;
import com.library.readspace.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @GetMapping
    @org.springframework.transaction.annotation.Transactional
    public List<Review> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();
        if (reviews.isEmpty()) {
            System.out.println("DEBUG: Review table is empty. Seeding sample data...");
            Review r1 = new Review();
            r1.setResourceId(1L);
            r1.setUserName("Alice Johnson");
            r1.setUserEmail("alice@example.com");
            r1.setRating(5);
            r1.setComment("This algorithm book is fantastic! Very clear explanations.");
            reviewRepository.save(r1);

            Review r2 = new Review();
            r2.setResourceId(2L);
            r2.setUserName("Bob Smith");
            r2.setUserEmail("bob@example.com");
            r2.setRating(4);
            r2.setComment("Really enjoyed the Deep Learning content. Very practical.");
            reviewRepository.save(r2);
            
            return reviewRepository.findAll();
        }
        return reviews;
    }

    @GetMapping("/resource/{id}")
    public List<Review> getReviewsByResource(@PathVariable Long id) {
        return reviewRepository.findByResourceId(id);
    }

    @PostMapping
    public Review addReview(@RequestBody Review review) {
        return reviewRepository.save(review);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        if (reviewRepository.existsById(id)) {
            reviewRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
