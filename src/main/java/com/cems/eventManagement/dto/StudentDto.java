package com.cems.eventManagement.dto;

import lombok.Data;

@Data
public class StudentDto {
    private Long id;
    private String name;
    private String email;
    private String course;
    private String rollNumber;
}
