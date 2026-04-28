package com.library.readspace.repository;

import com.library.readspace.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
    @Query("SELECT SUM(r.downloads) FROM Resource r")
    Integer getTotalDownloads();

    List<Resource> findTop5ByOrderByCreatedAtDesc();
}
