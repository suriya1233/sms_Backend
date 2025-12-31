package com.student.management.repository;

import com.student.management.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByStudentId(String studentId);

    List<Student> findTop10ByOrderByCreatedAtDesc();

    List<Student> findByClassName(String className);

    long countByClassName(String className);
}
