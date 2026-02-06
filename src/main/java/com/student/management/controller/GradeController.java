package com.student.management.controller;

import com.student.management.dto.GradeCreate;
import com.student.management.model.Grade;
import com.student.management.model.Student;
import com.student.management.model.UserAccount;
import com.student.management.repository.GradeRepository;
import com.student.management.repository.StudentRepository;
import com.student.management.repository.UserAccountRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grades")
public class GradeController {

    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final UserAccountRepository userRepository;

    public GradeController(GradeRepository gradeRepository,
            StudentRepository studentRepository,
            UserAccountRepository userRepository) {
        this.gradeRepository = gradeRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
    }

    // Student: Get my grades
    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getMyGrades() {
        UserAccount user = getCurrentUser();
        if (user == null || user.getStudentId() == null) {
            return ResponseEntity.status(403).body("Not a student account");
        }

        Student student = studentRepository.findByStudentId(user.getStudentId()).orElse(null);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }

        List<Grade> grades = gradeRepository.findByStudentOrderBySubject(student);
        Double averageMarks = gradeRepository.calculateAverageMarks(student);

        Map<String, Object> response = new HashMap<>();
        response.put("grades", grades);
        response.put("averageMarks", averageMarks != null ? Math.round(averageMarks * 100.0) / 100.0 : 0.0);
        response.put("overallGrade", calculateOverallGrade(averageMarks));

        return ResponseEntity.ok(response);
    }

    // Admin: Get all grades
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Grade> getAllGrades() {
        return gradeRepository.findAll();
    }

    // Admin: Get grades for a student
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getStudentGrades(@PathVariable String studentId) {
        Student student = studentRepository.findByStudentId(studentId).orElse(null);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }

        List<Grade> grades = gradeRepository.findByStudentOrderBySubject(student);
        Double averageMarks = gradeRepository.calculateAverageMarks(student);

        Map<String, Object> response = new HashMap<>();
        response.put("student", student);
        response.put("grades", grades);
        response.put("averageMarks", averageMarks != null ? Math.round(averageMarks * 100.0) / 100.0 : 0.0);
        response.put("overallGrade", calculateOverallGrade(averageMarks));

        return ResponseEntity.ok(response);
    }

    // Admin: Assign grade
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignGrade(@Valid @RequestBody GradeCreate dto) {
        Student student = studentRepository.findByStudentId(dto.getStudentId()).orElse(null);
        if (student == null) {
            return ResponseEntity.badRequest().body("Student not found");
        }

        // Check if grade already exists for this subject/semester
        if (dto.getSemester() != null &&
                gradeRepository.existsByStudentAndSubjectAndSemester(student, dto.getSubject(), dto.getSemester())) {
            return ResponseEntity.badRequest().body("Grade already exists for this subject and semester");
        }

        Grade grade = new Grade();
        grade.setStudent(student);
        grade.setSubject(dto.getSubject());
        grade.setMarks(dto.getMarks());
        grade.setMaxMarks(dto.getMaxMarks());
        grade.setSemester(dto.getSemester());
        grade.setGrade(Grade.calculateGrade(dto.getMarks(), dto.getMaxMarks()));

        Grade saved = gradeRepository.save(grade);

        // Update student's overall grade
        updateStudentOverallGrade(student);

        return ResponseEntity.ok(saved);
    }

    // Admin: Update grade
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateGrade(@PathVariable Long id, @RequestBody GradeCreate updates) {
        return gradeRepository.findById(id)
                .map(existing -> {
                    if (updates.getMarks() != null)
                        existing.setMarks(updates.getMarks());
                    if (updates.getMaxMarks() != null)
                        existing.setMaxMarks(updates.getMaxMarks());
                    if (updates.getSubject() != null)
                        existing.setSubject(updates.getSubject());
                    if (updates.getSemester() != null)
                        existing.setSemester(updates.getSemester());

                    Grade saved = gradeRepository.save(existing);
                    updateStudentOverallGrade(existing.getStudent());
                    return ResponseEntity.ok(saved);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Admin: Delete grade
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteGrade(@PathVariable Long id) {
        return gradeRepository.findById(id)
                .map(grade -> {
                    Student student = grade.getStudent();
                    gradeRepository.delete(grade);
                    updateStudentOverallGrade(student);
                    return ResponseEntity.ok().body("Grade deleted");
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Admin: Get grade summary by subject
    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Map<String, Object>> getGradeSummary() {
        List<Object[]> subjectAverages = gradeRepository.getAverageMarksBySubject();
        List<Map<String, Object>> summary = new ArrayList<>();

        for (Object[] row : subjectAverages) {
            Map<String, Object> item = new HashMap<>();
            item.put("subject", row[0]);
            item.put("averageMarks", row[1] != null ? Math.round((Double) row[1] * 100.0) / 100.0 : 0.0);
            summary.add(item);
        }

        return summary;
    }

    // Admin: Get all students with their overall grades
    @GetMapping("/students-summary")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Map<String, Object>> getStudentsGradeSummary() {
        List<Student> students = studentRepository.findAll();
        List<Map<String, Object>> summary = new ArrayList<>();

        for (Student student : students) {
            Double averageMarks = gradeRepository.calculateAverageMarks(student);

            Map<String, Object> item = new HashMap<>();
            item.put("studentId", student.getStudentId());
            item.put("name", student.getName());
            item.put("className", student.getClassName());
            item.put("averageMarks", averageMarks != null ? Math.round(averageMarks * 100.0) / 100.0 : 0.0);
            item.put("overallGrade", calculateOverallGrade(averageMarks));
            summary.add(item);
        }

        return summary;
    }

    private void updateStudentOverallGrade(Student student) {
        Double averageMarks = gradeRepository.calculateAverageMarks(student);
        String overallGrade = calculateOverallGrade(averageMarks);
        student.setGrade(overallGrade);
        studentRepository.save(student);
    }

    private String calculateOverallGrade(Double averageMarks) {
        if (averageMarks == null)
            return "N/A";
        if (averageMarks >= 90)
            return "A+";
        if (averageMarks >= 80)
            return "A";
        if (averageMarks >= 70)
            return "B+";
        if (averageMarks >= 60)
            return "B";
        if (averageMarks >= 50)
            return "C";
        if (averageMarks >= 40)
            return "D";
        return "F";
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
