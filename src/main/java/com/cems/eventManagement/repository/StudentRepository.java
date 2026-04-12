package com.cems.eventManagement.repository;

import com.cems.eventManagement.entity.Student;
import org.hibernate.query.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student,Long> {

    List<Student> findStudentsByCourse(String course);

    boolean existsByEmail(String email);

    Optional<Student> findByEmail(String email);

}
