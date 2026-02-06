package com.student.management.controller;

import com.student.management.dto.LeaveRequestCreate;
import com.student.management.dto.LeaveStatusUpdate;
import com.student.management.model.LeaveRequest;
import com.student.management.model.Student;
import com.student.management.model.UserAccount;
import com.student.management.repository.LeaveRequestRepository;
import com.student.management.repository.StudentRepository;
import com.student.management.repository.UserAccountRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
public class LeaveController {

    private final LeaveRequestRepository leaveRepo;
    private final StudentRepository studentRepo;
    private final UserAccountRepository userRepo;

    public LeaveController(LeaveRequestRepository leaveRepo, StudentRepository studentRepo,
            UserAccountRepository userRepo) {
        this.leaveRepo = leaveRepo;
        this.studentRepo = studentRepo;
        this.userRepo = userRepo;
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> create(@Valid @RequestBody LeaveRequestCreate payload) {
        UserAccount user = getCurrentUser();
        if (user == null || user.getStudentId() == null) {
            return ResponseEntity.status(403).body("Not a student account");
        }
        Student student = studentRepo.findByStudentId(user.getStudentId()).orElse(null);
        if (student == null) {
            return ResponseEntity.status(404).body("Student not found");
        }
        LeaveRequest req = new LeaveRequest();
        req.setStudent(student);
        req.setStartDate(payload.getStartDate());
        req.setEndDate(payload.getEndDate());
        req.setReason(payload.getReason());
        LeaveRequest saved = leaveRepo.save(req);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> myLeaves() {
        UserAccount user = getCurrentUser();
        if (user == null || user.getStudentId() == null) {
            return ResponseEntity.status(403).body("Not a student account");
        }
        Student student = studentRepo.findByStudentId(user.getStudentId()).orElse(null);
        if (student == null) {
            return ResponseEntity.status(404).body("Student not found");
        }
        return ResponseEntity.ok(leaveRepo.findByStudent(student));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<LeaveRequest> all() {
        return leaveRepo.findAll();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @Valid @RequestBody LeaveStatusUpdate update) {
        if (id == null) {
            return ResponseEntity.badRequest().body("ID cannot be null");
        }
        return leaveRepo.findById(id)
                .map(existing -> {
                    existing.setStatus(update.getStatus());
                    return ResponseEntity.ok(leaveRepo.save(existing));
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
        return userRepo.findByUsername(principal.toString()).orElse(null);
    }
}
