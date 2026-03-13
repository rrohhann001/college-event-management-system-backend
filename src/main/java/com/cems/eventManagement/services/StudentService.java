package com.cems.eventManagement.services;

import com.cems.eventManagement.dto.StudentDto;
import com.cems.eventManagement.entity.Student;
import com.cems.eventManagement.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;


    public Student registerStudent(Student student){
        return studentRepository.save(student);
    }

    public List<StudentDto> getAllStudents(){
        List<Student> students = studentRepository.findAll();

        List<StudentDto> studentDtos=new ArrayList<>();

        for(Student student:students){

            StudentDto dto=new StudentDto();
            dto.setId(student.getId());
            dto.setName(student.getName());
            dto.setCourse(student.getCourse());
            dto.setEmail(student.getEmail());
            dto.setRollNumber(student.getRollNumber());

            studentDtos.add(dto);

        }
        return studentDtos;
    }

    public StudentDto getStudentById(Long id){
        Student student = studentRepository.findById(id).orElseThrow(()-> new RuntimeException("Student not found"));
        StudentDto dto=new StudentDto();
        dto.setId(student.getId());
        dto.setName(student.getName());
        dto.setCourse(student.getCourse());
        dto.setEmail(student.getEmail());
        dto.setRollNumber(student.getRollNumber());

        return dto;

    }

    public void deleteStudent(Long id){
        studentRepository.deleteById(id);
    }

    public List<StudentDto> getStudentsByCourse(String course) {
        List<Student> students= studentRepository.findStudentsByCourse(course);

        List<StudentDto> studentDtos=new ArrayList<>();

        for(Student student:students){

            StudentDto dto=new StudentDto();
            dto.setId(student.getId());
            dto.setName(student.getName());
            dto.setCourse(student.getCourse());
            dto.setEmail(student.getEmail());
            dto.setRollNumber(student.getRollNumber());

            studentDtos.add(dto);

        }
        return studentDtos;

    }
}
