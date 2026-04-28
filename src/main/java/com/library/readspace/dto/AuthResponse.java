package com.library.readspace.dto;

import com.library.readspace.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

public class AuthResponse {
    private boolean success;
    private String error;
    private User user;
    private String token;

    public AuthResponse() {}

    public AuthResponse(boolean success, String error, User user, String token) {
        this.success = success;
        this.error = error;
        this.user = user;
        this.token = token;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
