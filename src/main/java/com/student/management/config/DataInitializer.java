package com.student.management.config;

import com.student.management.model.Student;
import com.student.management.model.UserAccount;
import com.student.management.model.UserRole;
import com.student.management.repository.StudentRepository;
import com.student.management.repository.UserAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(UserAccountRepository userRepo,
                               StudentRepository studentRepo,
                               PasswordEncoder encoder) {
        return args -> {
            if (studentRepo.count() == 0) {
                Student s1 = new Student();
                s1.setStudentId("S001");
                s1.setName("John Doe");
                s1.setClassName("10-A");
                s1.setAttendancePercent(92.0);
                s1.setGrade("A");

                Student s2 = new Student();
                s2.setStudentId("S002");
                s2.setName("Jane Smith");
                s2.setClassName("10-A");
                s2.setAttendancePercent(88.0);
                s2.setGrade("B+");

                studentRepo.save(s1);
                studentRepo.save(s2);
            }

            if (userRepo.count() == 0) {
                UserAccount admin = new UserAccount();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRole(UserRole.ADMIN);
                userRepo.save(admin);

                Student firstStudent = studentRepo.findByStudentId("S001").orElse(null);
                if (firstStudent != null) {
                    UserAccount studentUser = new UserAccount();
                    studentUser.setUsername("S001");
                    studentUser.setPassword(encoder.encode("password"));
                    studentUser.setRole(UserRole.STUDENT);
                    studentUser.setStudentId(firstStudent.getStudentId());
                    userRepo.save(studentUser);
                }
            }
        };
    }
}

