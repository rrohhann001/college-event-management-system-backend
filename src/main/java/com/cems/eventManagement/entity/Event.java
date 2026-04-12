package com.cems.eventManagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name="events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "event_name", nullable = false)
    private String eventName;

    @Column(nullable = false)
    private String description;

    @Column(name = "event_date")
    private LocalDate eventDate;

    @Column(nullable = false)
    private String location;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    @JsonIgnore
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Registration> registrations;


    // Default Constructor
    public Event() {
    }



}
