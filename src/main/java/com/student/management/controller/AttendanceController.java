package com.student.management.controller;

import com.student.management.dto.AttendanceCreate;
import com.student.management.dto.BulkAttendanceCreate;
import com.student.management.model.Attendance;
import com.student.management.model.Student;
import com.student.management.model.UserAccount;
import com.student.management.repository.AttendanceRepository;
import com.student.management.repository.StudentRepository;
import com.student.management.repository.UserAccountRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")

@CrossOrigin(origins = "*")

public class AttendanceController {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final UserAccountRepository userRepository;

    public AttendanceController(AttendanceRepository attendanceRepository,
            StudentRepository studentRepository,
            UserAccountRepository userRepository) {
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
    }

    // Student: Get my attendance
    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getMyAttendance() {
        UserAccount user = getCurrentUser();
        if (user == null || user.getStudentId() == null) {
            return ResponseEntity.status(403).body("Not a student account");
        }

        Student student = studentRepository.findByStudentId(user.getStudentId()).orElse(null);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }

        List<Attendance> attendance = attendanceRepository.findByStudentOrderByDateDesc(student);
        Long presentDays = attendanceRepository.countPresentDays(student);
        Long totalDays = attendanceRepository.countTotalDays(student);

        double percentage = totalDays > 0 ? (presentDays * 100.0 / totalDays) : 0.0;

        Map<String, Object> response = new HashMap<>();
        response.put("attendance", attendance);
        response.put("presentDays", presentDays);
        response.put("totalDays", totalDays);
        response.put("percentage", Math.round(percentage * 100.0) / 100.0);

        return ResponseEntity.ok(response);
    }

    // Admin: Get all attendance for a date
    @GetMapping("/date/{date}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Attendance> getAttendanceByDate(@PathVariable String date) {
        LocalDate localDate = LocalDate.parse(date);
        return attendanceRepository.findByDate(localDate);
    }

    // Admin: Get attendance for a student
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getStudentAttendance(@PathVariable String studentId) {
        Student student = studentRepository.findByStudentId(studentId).orElse(null);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }

        List<Attendance> attendance = attendanceRepository.findByStudentOrderByDateDesc(student);
        Long presentDays = attendanceRepository.countPresentDays(student);
        Long totalDays = attendanceRepository.countTotalDays(student);

        double percentage = totalDays > 0 ? (presentDays * 100.0 / totalDays) : 0.0;

        Map<String, Object> response = new HashMap<>();
        response.put("student", student);
        response.put("attendance", attendance);
        response.put("presentDays", presentDays);
        response.put("totalDays", totalDays);
        response.put("percentage", Math.round(percentage * 100.0) / 100.0);

        return ResponseEntity.ok(response);
    }

    // Admin: Record single attendance
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> recordAttendance(@Valid @RequestBody AttendanceCreate dto) {
        Student student = studentRepository.findByStudentId(dto.getStudentId()).orElse(null);
        if (student == null) {
            return ResponseEntity.badRequest().body("Student not found");
        }

        // Check if attendance already exists for this date
        if (attendanceRepository.existsByStudentAndDate(student, dto.getDate())) {
            return ResponseEntity.badRequest().body("Attendance already recorded for this date");
        }

        Attendance attendance = new Attendance();
        attendance.setStudent(student);
        attendance.setDate(dto.getDate());
        attendance.setPresent(dto.getPresent());
        attendance.setRemarks(dto.getRemarks());

        Attendance saved = attendanceRepository.save(attendance);

        // Update student's attendance percentage
        updateStudentAttendancePercent(student);

        return ResponseEntity.ok(saved);
    }

    // Admin: Record bulk attendance for a date
    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> recordBulkAttendance(@Valid @RequestBody BulkAttendanceCreate dto) {
        List<Attendance> savedList = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (BulkAttendanceCreate.StudentAttendance record : dto.getRecords()) {
            Student student = studentRepository.findByStudentId(record.getStudentId()).orElse(null);
            if (student == null) {
                errors.add("Student not found: " + record.getStudentId());
                continue;
            }

            // Skip if attendance already exists
            if (attendanceRepository.existsByStudentAndDate(student, dto.getDate())) {
                errors.add("Attendance already exists for: " + record.getStudentId());
                continue;
            }

            Attendance attendance = new Attendance();
            attendance.setStudent(student);
            attendance.setDate(dto.getDate());
            attendance.setPresent(record.getPresent());
            attendance.setRemarks(record.getRemarks());

            savedList.add(attendanceRepository.save(attendance));
            updateStudentAttendancePercent(student);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("saved", savedList.size());
        response.put("errors", errors);

        return ResponseEntity.ok(response);
    }

    // Admin: Update attendance
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateAttendance(@PathVariable Long id, @RequestBody Attendance updates) {
        return attendanceRepository.findById(id)
                .map(existing -> {
                    if (updates.isPresent() != existing.isPresent()) {
                        existing.setPresent(updates.isPresent());
                    }
                    if (updates.getRemarks() != null) {
                        existing.setRemarks(updates.getRemarks());
                    }
                    Attendance saved = attendanceRepository.save(existing);
                    updateStudentAttendancePercent(existing.getStudent());
                    return ResponseEntity.ok(saved);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Admin: Delete attendance
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteAttendance(@PathVariable Long id) {
        return attendanceRepository.findById(id)
                .map(attendance -> {
                    Student student = attendance.getStudent();
                    attendanceRepository.delete(attendance);
                    updateStudentAttendancePercent(student);
                    return ResponseEntity.ok().body("Attendance deleted");
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Admin: Get all students with their attendance percentage
    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Map<String, Object>> getAttendanceSummary() {
        List<Student> students = studentRepository.findAll();
        List<Map<String, Object>> summary = new ArrayList<>();

        for (Student student : students) {
            Long presentDays = attendanceRepository.countPresentDays(student);
            Long totalDays = attendanceRepository.countTotalDays(student);
            double percentage = totalDays > 0 ? (presentDays * 100.0 / totalDays) : 0.0;

            Map<String, Object> item = new HashMap<>();
            item.put("studentId", student.getStudentId());
            item.put("name", student.getName());
            item.put("className", student.getClassName());
            item.put("presentDays", presentDays);
            item.put("totalDays", totalDays);
            item.put("percentage", Math.round(percentage * 100.0) / 100.0);
            summary.add(item);
        }

        return summary;
    }

    private void updateStudentAttendancePercent(Student student) {
        Long presentDays = attendanceRepository.countPresentDays(student);
        Long totalDays = attendanceRepository.countTotalDays(student);
        double percentage = totalDays > 0 ? (presentDays * 100.0 / totalDays) : 0.0;
        student.setAttendancePercent(Math.round(percentage * 100.0) / 100.0);
        studentRepository.save(student);
    }

    private UserAccount getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                : null;
        if (principal == null) {
            return null;
        }
        return userRepository.findByUsername(principal.toString()).orElse(null);
    }
}
