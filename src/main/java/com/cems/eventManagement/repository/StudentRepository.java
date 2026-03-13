package com.cems.eventManagement.repository;

import com.cems.eventManagement.dto.StudentDto;
import com.cems.eventManagement.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student,Long> {

    List<Student> findStudentsByCourse(String course);

    Optional<Student> findByEmail(String email);

}
