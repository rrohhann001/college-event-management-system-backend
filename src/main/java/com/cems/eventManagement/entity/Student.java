package com.cems.eventManagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name="students")
@NoArgsConstructor
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name cannot be empty")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Please enter a valid email")
    @Column(nullable = false,unique = true)
    private String email;

    @NotBlank(message = "Course cannot be empty")
    @Column(nullable = false)
    private String course;

    @NotBlank(message="Password cannot be empty")
    @Size(min = 6,message="Password must be at least 6 characters")
    @Column(nullable = false)
    @JsonProperty(access=JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotBlank(message = "Roll Number cannot be empty")
    @Column(nullable = false,unique = true)
    private String rollNumber;

    @Column(nullable = false)
    private String role="Student";
}
