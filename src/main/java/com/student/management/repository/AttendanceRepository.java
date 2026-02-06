package com.student.management.repository;

import com.student.management.model.Attendance;
import com.student.management.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByStudent(Student student);

    List<Attendance> findByStudentOrderByDateDesc(Student student);

    List<Attendance> findByDate(LocalDate date);

    List<Attendance> findByStudentAndDateBetween(Student student, LocalDate startDate, LocalDate endDate);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student = :student AND a.present = true")
    Long countPresentDays(@Param("student") Student student);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student = :student")
    Long countTotalDays(@Param("student") Student student);

    boolean existsByStudentAndDate(Student student, LocalDate date);
}
