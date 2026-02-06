package com.student.management.repository;

import com.student.management.model.LeaveRequest;
import com.student.management.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByStudent(Student student);
}

