package com.cems.eventManagement.repository;

import com.cems.eventManagement.entity.Event;
import com.cems.eventManagement.entity.Registration;
import com.cems.eventManagement.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration,Long> {

    List<Registration> findByEventId(Long eventId);

    List<Registration> findByStudentId(Long studentId);

    Optional<Registration> findByStudentIdAndEventId(Long studentId, Long eventId);
}
