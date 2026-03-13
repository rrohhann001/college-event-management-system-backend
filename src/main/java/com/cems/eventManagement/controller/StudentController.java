package com.cems.eventManagement.controller;

import com.cems.eventManagement.dto.StudentDto;
import com.cems.eventManagement.entity.Student;
import com.cems.eventManagement.repository.StudentRepository;
import com.cems.eventManagement.services.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("api/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @PostMapping
    public Student registerStudent(@Valid @RequestBody Student student){
        return studentService.registerStudent(student);
    }

    @GetMapping
    public List<StudentDto> getAllStudents(){
        return studentService.getAllStudents();
    }

    @GetMapping("/{id}")
    public StudentDto getStudentById(@PathVariable Long id){
        return studentService.getStudentById(id);
    }

    @GetMapping("/course/{courseName}")
    public List<StudentDto> getStudentsByCourse(@PathVariable String courseName){
        return studentService.getStudentsByCourse(courseName);
    }

    @GetMapping("/my-profile")
    public StudentDto getMyProfile(Principal principal) {
        String loggedInEmail = principal.getName();

        Student student = studentRepository.findByEmail(loggedInEmail)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        StudentDto dto = new StudentDto();
        dto.setId(student.getId());
        dto.setName(student.getName());
        dto.setEmail(student.getEmail());
        dto.setCourse(student.getCourse());
        dto.setRollNumber(student.getRollNumber());

        return dto;
    }

    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable Long id){
        studentService.deleteStudent(id);
        return "Student with ID " + id + " has been deleted.";
    }
}
