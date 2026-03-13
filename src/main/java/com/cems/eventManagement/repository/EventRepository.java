package com.cems.eventManagement.repository;

import com.cems.eventManagement.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByEventDate(String eventDate);
}
