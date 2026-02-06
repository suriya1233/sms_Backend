package com.student.management.controller;

import com.student.management.dto.DashboardSummary;
import com.student.management.repository.AttendanceRepository;
import com.student.management.repository.LeaveRequestRepository;
import com.student.management.repository.StudentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

        private final StudentRepository studentRepository;
        private final LeaveRequestRepository leaveRepository;
        private final AttendanceRepository attendanceRepository;

        public DashboardController(StudentRepository studentRepository,
                        LeaveRequestRepository leaveRepository,
                        AttendanceRepository attendanceRepository) {
                this.studentRepository = studentRepository;
                this.leaveRepository = leaveRepository;
                this.attendanceRepository = attendanceRepository;
        }

        @GetMapping("/summary")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<DashboardSummary> getDashboardSummary() {
                DashboardSummary summary = new DashboardSummary();

                long totalStudents = studentRepository.count();
                long pendingLeaves = leaveRepository.findAll().stream()
                                .filter(leave -> leave.getStatus().name().equals("PENDING"))
                                .count();

                summary.setTotalStudents(totalStudents);
                summary.setPendingLeaveRequests(pendingLeaves);

                return ResponseEntity.ok(summary);
        }

        @GetMapping("/stats")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<?> getStats() {
                long totalStudents = studentRepository.count();

                // Count unique classes
                long totalClasses = studentRepository.findAll().stream()
                                .map(student -> student.getClassName())
                                .distinct()
                                .count();

                // Calculate average attendance
                Double averageAttendance = studentRepository.findAll().stream()
                                .filter(student -> student.getAttendancePercent() != null)
                                .mapToDouble(student -> student.getAttendancePercent())
                                .average()
                                .orElse(0.0);

                // Count pending leaves
                long pendingLeaves = leaveRepository.findAll().stream()
                                .filter(leave -> leave.getStatus().name().equals("PENDING"))
                                .count();

                java.util.Map<String, Object> stats = new java.util.HashMap<>();
                stats.put("totalStudents", totalStudents);
                stats.put("totalClasses", totalClasses);
                stats.put("averageAttendance", Math.round(averageAttendance * 100.0) / 100.0);
                stats.put("pendingLeaves", pendingLeaves);

                return ResponseEntity.ok(stats);
        }
}
