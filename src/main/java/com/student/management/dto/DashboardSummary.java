package com.student.management.dto;

import java.util.Map;

public class DashboardSummary {

    private long totalStudents;
    private double averageAttendance;
    private Map<String, Long> gradeDistribution;
    private long pendingLeaveRequests;
    private long totalClasses;
    private Map<String, Double> subjectAverages;

    // Constructors
    public DashboardSummary() {
    }

    public DashboardSummary(long totalStudents, double averageAttendance,
            Map<String, Long> gradeDistribution, long pendingLeaveRequests) {
        this.totalStudents = totalStudents;
        this.averageAttendance = averageAttendance;
        this.gradeDistribution = gradeDistribution;
        this.pendingLeaveRequests = pendingLeaveRequests;
    }

    // Getters and Setters
    public long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public double getAverageAttendance() {
        return averageAttendance;
    }

    public void setAverageAttendance(double averageAttendance) {
        this.averageAttendance = averageAttendance;
    }

    public Map<String, Long> getGradeDistribution() {
        return gradeDistribution;
    }

    public void setGradeDistribution(Map<String, Long> gradeDistribution) {
        this.gradeDistribution = gradeDistribution;
    }

    public long getPendingLeaveRequests() {
        return pendingLeaveRequests;
    }

    public void setPendingLeaveRequests(long pendingLeaveRequests) {
        this.pendingLeaveRequests = pendingLeaveRequests;
    }

    public long getTotalClasses() {
        return totalClasses;
    }

    public void setTotalClasses(long totalClasses) {
        this.totalClasses = totalClasses;
    }

    public Map<String, Double> getSubjectAverages() {
        return subjectAverages;
    }

    public void setSubjectAverages(Map<String, Double> subjectAverages) {
        this.subjectAverages = subjectAverages;
    }
}
