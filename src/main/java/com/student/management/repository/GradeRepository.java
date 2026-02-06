package com.student.management.repository;

import com.student.management.model.Grade;
import com.student.management.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, Long> {

    List<Grade> findByStudent(Student student);

    List<Grade> findByStudentOrderBySubject(Student student);

    List<Grade> findByStudentAndSemester(Student student, String semester);

    List<Grade> findBySubject(String subject);

    @Query("SELECT AVG(g.marks) FROM Grade g WHERE g.student = :student")
    Double calculateAverageMarks(@Param("student") Student student);

    @Query("SELECT g.subject, AVG(g.marks) FROM Grade g GROUP BY g.subject")
    List<Object[]> getAverageMarksBySubject();

    boolean existsByStudentAndSubjectAndSemester(Student student, String subject, String semester);
}
