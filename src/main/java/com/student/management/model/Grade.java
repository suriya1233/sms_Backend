package com.student.management.model;

import jakarta.persistence.*;

@Entity
@Table(name = "grades")
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", referencedColumnName = "student_id")
    private Student student;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private Double marks;

    @Column(nullable = false)
    private Double maxMarks = 100.0;

    private String grade;

    @Column(length = 50)
    private String semester;

    // Constructors
    public Grade() {
    }

    public Grade(Student student, String subject, Double marks, Double maxMarks) {
        this.student = student;
        this.subject = subject;
        this.marks = marks;
        this.maxMarks = maxMarks;
        this.grade = calculateGrade(marks, maxMarks);
    }

    // Calculate grade based on percentage
    public static String calculateGrade(Double marks, Double maxMarks) {
        if (maxMarks == null || maxMarks == 0)
            return "N/A";
        double percentage = (marks / maxMarks) * 100;
        if (percentage >= 90)
            return "A+";
        if (percentage >= 80)
            return "A";
        if (percentage >= 70)
            return "B+";
        if (percentage >= 60)
            return "B";
        if (percentage >= 50)
            return "C";
        if (percentage >= 40)
            return "D";
        return "F";
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Double getMarks() {
        return marks;
    }

    public void setMarks(Double marks) {
        this.marks = marks;
        this.grade = calculateGrade(marks, this.maxMarks);
    }

    public Double getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(Double maxMarks) {
        this.maxMarks = maxMarks;
        this.grade = calculateGrade(this.marks, maxMarks);
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }
}
