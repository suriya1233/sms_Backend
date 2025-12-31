package com.student.management.controller;

import com.student.management.dto.DashboardSummary;
import com.student.management.model.LeaveStatus;
import com.student.management.model.Student;
import com.student.management.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

        private final StudentRepository studentRepository;
        private final GradeRepository gradeRepository;
        private final LeaveRequestRepository leaveRepository;

        public DashboardController(StudentRepository studentRepository,
                        GradeRepository gradeRepository,
                        LeaveRequestRepository leaveRepository) {
                this.studentRepository = studentRepository;
                this.gradeRepository = gradeRepository;
                this.leaveRepository = leaveRepository;
        }

        @GetMapping("/summary")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<DashboardSummary> getDashboardSummary() {
                DashboardSummary summary = new DashboardSummary();

                // Total students
                long totalStudents = studentRepository.count();
                summary.setTotalStudents(totalStudents);

                // Calculate average attendance
                List<Student> students = studentRepository.findAll();
                double avgAttendance = students.stream()
                                .filter(s -> s.getAttendancePercent() != null)
                                .mapToDouble(Student::getAttendancePercent)
                                .average()
                                .orElse(0.0);
                summary.setAverageAttendance(Math.round(avgAttendance * 100.0) / 100.0);

                // Grade distribution
                Map<String, Long> gradeDistribution = new HashMap<>();
                gradeDistribution.put("A+", students.stream().filter(s -> "A+".equals(s.getGrade())).count());
                gradeDistribution.put("A", students.stream().filter(s -> "A".equals(s.getGrade())).count());
                gradeDistribution.put("B+", students.stream().filter(s -> "B+".equals(s.getGrade())).count());
                gradeDistribution.put("B", students.stream().filter(s -> "B".equals(s.getGrade())).count());
                gradeDistribution.put("C", students.stream().filter(s -> "C".equals(s.getGrade())).count());
                gradeDistribution.put("D", students.stream().filter(s -> "D".equals(s.getGrade())).count());
                gradeDistribution.put("F", students.stream().filter(s -> "F".equals(s.getGrade())).count());
                summary.setGradeDistribution(gradeDistribution);

                // Pending leave requests
                long pendingLeaves = leaveRepository.findAll().stream()
                                .filter(l -> l.getStatus() == LeaveStatus.PENDING)
                                .count();
                summary.setPendingLeaveRequests(pendingLeaves);

                // Total unique classes
                long totalClasses = students.stream()
                                .map(Student::getClassName)
                                .distinct()
                                .count();
                summary.setTotalClasses(totalClasses);

                // Subject averages
                List<Object[]> subjectAvgs = gradeRepository.getAverageMarksBySubject();
                Map<String, Double> subjectAverages = new HashMap<>();
                for (Object[] row : subjectAvgs) {
                        if (row[0] != null && row[1] != null) {
                                subjectAverages.put((String) row[0], Math.round((Double) row[1] * 100.0) / 100.0);
                        }
                }
                summary.setSubjectAverages(subjectAverages);

                return ResponseEntity.ok(summary);
        }

        @GetMapping("/stats")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<?> getQuickStats() {
                Map<String, Object> stats = new HashMap<>();

                // Basic counts
                stats.put("totalStudents", studentRepository.count());
                stats.put("totalLeaveRequests", leaveRepository.count());
                stats.put("pendingLeaves", leaveRepository.findAll().stream()
                                .filter(l -> l.getStatus() == LeaveStatus.PENDING).count());
                stats.put("approvedLeaves", leaveRepository.findAll().stream()
                                .filter(l -> l.getStatus() == LeaveStatus.APPROVED).count());

                // Average attendance
                List<Student> students = studentRepository.findAll();
                double avgAttendance = students.stream()
                                .filter(s -> s.getAttendancePercent() != null)
                                .mapToDouble(Student::getAttendancePercent)
                                .average()
                                .orElse(0.0);
                stats.put("averageAttendance", Math.round(avgAttendance * 100.0) / 100.0);

                // Classes
                List<String> classes = students.stream()
                                .map(Student::getClassName)
                                .distinct()
                                .collect(Collectors.toList());
                stats.put("classes", classes);
                stats.put("totalClasses", classes.size());

                return ResponseEntity.ok(stats);
        }

        @GetMapping("/class/{className}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<?> getClassStats(@PathVariable String className) {
                List<Student> classStudents = studentRepository.findByClassName(className);

                if (classStudents.isEmpty()) {
                        return ResponseEntity.notFound().build();
                }

                Map<String, Object> stats = new HashMap<>();
                stats.put("className", className);
                stats.put("totalStudents", classStudents.size());
                stats.put("students", classStudents);

                // Average attendance for class
                double avgAttendance = classStudents.stream()
                                .filter(s -> s.getAttendancePercent() != null)
                                .mapToDouble(Student::getAttendancePercent)
                                .average()
                                .orElse(0.0);
                stats.put("averageAttendance", Math.round(avgAttendance * 100.0) / 100.0);

                // Grade distribution for class
                Map<String, Long> gradeDistribution = new HashMap<>();
                gradeDistribution.put("A+", classStudents.stream().filter(s -> "A+".equals(s.getGrade())).count());
                gradeDistribution.put("A", classStudents.stream().filter(s -> "A".equals(s.getGrade())).count());
                gradeDistribution.put("B+", classStudents.stream().filter(s -> "B+".equals(s.getGrade())).count());
                gradeDistribution.put("B", classStudents.stream().filter(s -> "B".equals(s.getGrade())).count());
                gradeDistribution.put("C", classStudents.stream().filter(s -> "C".equals(s.getGrade())).count());
                gradeDistribution.put("D", classStudents.stream().filter(s -> "D".equals(s.getGrade())).count());
                gradeDistribution.put("F", classStudents.stream().filter(s -> "F".equals(s.getGrade())).count());
                stats.put("gradeDistribution", gradeDistribution);

                return ResponseEntity.ok(stats);
        }
}
