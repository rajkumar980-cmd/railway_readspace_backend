package com.library.readspace.repository;

import com.library.readspace.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByResourceId(Long resourceId);
    List<Review> findTop5ByOrderByCreatedAtDesc();
}
