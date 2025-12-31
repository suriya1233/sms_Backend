package com.student.management.controller;

import com.student.management.dto.StudentCreate;
import com.student.management.model.Student;
import com.student.management.model.UserAccount;
import com.student.management.model.UserRole;
import com.student.management.repository.StudentRepository;
import com.student.management.repository.UserAccountRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentRepository studentRepository;
    private final UserAccountRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentController(StudentRepository studentRepository,
            UserAccountRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/me")
    public ResponseEntity<?> currentStudent() {
        UserAccount user = getCurrentUser();
        if (user == null || user.getStudentId() == null) {
            return ResponseEntity.status(403).body("Not a student account");
        }
        return studentRepository.findByStudentId(user.getStudentId())
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Student> listStudents() {
        return studentRepository.findAll();
    }

    @GetMapping("/recent")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Student> getRecentStudents() {
        return studentRepository.findTop10ByOrderByCreatedAtDesc();
    }

    @GetMapping("/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getStudent(@PathVariable String studentId) {
        return studentRepository.findByStudentId(studentId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addStudent(@Valid @RequestBody StudentCreate dto) {
        // Check if student ID already exists
        if (studentRepository.findByStudentId(dto.getStudentId()).isPresent()) {
            return ResponseEntity.badRequest().body("Student ID already exists");
        }

        // Check if username already exists
        if (userRepository.findByUsername(dto.getStudentId()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        // Create student
        Student student = new Student();
        student.setStudentId(dto.getStudentId());
        student.setName(dto.getName());
        student.setClassName(dto.getClassName());
        student.setEmail(dto.getEmail());
        student.setPhone(dto.getPhone());
        student.setAttendancePercent(0.0);
        student.setGrade("N/A");

        Student savedStudent = studentRepository.save(student);

        // Create user account for the student
        UserAccount userAccount = new UserAccount();
        userAccount.setUsername(dto.getStudentId());
        userAccount.setPassword(passwordEncoder.encode(dto.getPassword()));
        userAccount.setRole(UserRole.STUDENT);
        userAccount.setStudentId(dto.getStudentId());
        userRepository.save(userAccount);

        return ResponseEntity.ok(savedStudent);
    }

    @PutMapping("/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateStudent(@PathVariable String studentId, @RequestBody Student updates) {
        return studentRepository.findByStudentId(studentId)
                .map(existing -> {
                    if (updates.getName() != null)
                        existing.setName(updates.getName());
                    if (updates.getClassName() != null)
                        existing.setClassName(updates.getClassName());
                    if (updates.getEmail() != null)
                        existing.setEmail(updates.getEmail());
                    if (updates.getPhone() != null)
                        existing.setPhone(updates.getPhone());
                    if (updates.getAttendancePercent() != null)
                        existing.setAttendancePercent(updates.getAttendancePercent());
                    if (updates.getGrade() != null)
                        existing.setGrade(updates.getGrade());
                    return ResponseEntity.ok(studentRepository.save(existing));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteStudent(@PathVariable String studentId) {
        return studentRepository.findByStudentId(studentId)
                .map(student -> {
                    // Delete associated user account
                    userRepository.findByUsername(studentId).ifPresent(userRepository::delete);
                    // Delete student
                    studentRepository.delete(student);
                    return ResponseEntity.ok().body("Student deleted successfully");
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private UserAccount getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                : null;
        if (principal == null) {
            return null;
        }
        String username = principal.toString();
        return userRepository.findByUsername(username).orElse(null);
    }
}
