package com.student.management.controller;

import com.student.management.dto.LoginRequest;
import com.student.management.dto.LoginResponse;
import com.student.management.model.Student;
import com.student.management.model.UserAccount;
import com.student.management.repository.StudentRepository;
import com.student.management.repository.UserAccountRepository;
import com.student.management.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserAccountRepository userRepo;
    private final StudentRepository studentRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserAccountRepository userRepo, StudentRepository studentRepo,
            PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepo = userRepo;
        this.studentRepo = studentRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        System.out.println("Login Params: " + request.getUsername() + ", " + request.getPassword());
        UserAccount user = userRepo.findByUsername(request.getUsername())
                .orElse(null);
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        String token = jwtService.generateToken(user);
        // Get the student name if this is a student account
        String name = user.getUsername();
        if (user.getStudentId() != null) {
            Student student = studentRepo.findByStudentId(user.getStudentId()).orElse(null);
            if (student != null) {
                name = student.getName();
            }
        } else if (user.getRole().name().equals("ADMIN")) {
            name = "Administrator";
        }

        return ResponseEntity
                .ok(new LoginResponse(token, user.getRole().name(), user.getUsername(), user.getStudentId(), name));
    }
}
