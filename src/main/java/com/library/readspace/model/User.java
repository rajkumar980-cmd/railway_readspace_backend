package com.library.readspace.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    @Column(unique = true)
    private String email;
    
    @com.fasterxml.jackson.annotation.JsonIgnore
    private String password;
    private String role;
    private LocalDate joined;
    private Integer downloads;
    private String status;

    public User() {}

    public User(Long id, String name, String email, String password, String role, LocalDate joined, Integer downloads, String status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.joined = joined;
        this.downloads = downloads;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDate getJoined() { return joined; }
    public void setJoined(LocalDate joined) { this.joined = joined; }

    public Integer getDownloads() { return downloads; }
    public void setDownloads(Integer downloads) { this.downloads = downloads; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
