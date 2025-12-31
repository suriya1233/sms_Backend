package com.student.management.dto;

public class LoginResponse {
    private String token;
    private String role;
    private String username;
    private String studentId;
    private String name;

    public LoginResponse(String token, String role, String username, String studentId, String name) {
        this.token = token;
        this.role = role;
        this.username = username;
        this.studentId = studentId;
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public String getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }
}
