package com.cems.eventManagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name="registration")
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="student_id",nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name="event_id",nullable = false)
    private Event event;




}
