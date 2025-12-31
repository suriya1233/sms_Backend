package com.student.management.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.time.LocalDate;

public class BulkAttendanceCreate {

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Attendance records are required")
    private List<StudentAttendance> records;

    public static class StudentAttendance {
        private String studentId;
        private Boolean present;
        private String remarks;

        public String getStudentId() {
            return studentId;
        }

        public void setStudentId(String studentId) {
            this.studentId = studentId;
        }

        public Boolean getPresent() {
            return present;
        }

        public void setPresent(Boolean present) {
            this.present = present;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }
    }

    // Getters and Setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<StudentAttendance> getRecords() {
        return records;
    }

    public void setRecords(List<StudentAttendance> records) {
        this.records = records;
    }
}
