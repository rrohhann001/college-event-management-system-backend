package com.cems.eventManagement.services;

import com.cems.eventManagement.dto.LoginRequest;
import com.cems.eventManagement.dto.LoginResponse;
import com.cems.eventManagement.entity.Student;
import com.cems.eventManagement.repository.StudentRepository;
import com.cems.eventManagement.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private StudentRepository studentRepository;

    public LoginResponse login(LoginRequest request){
        Student student=studentRepository.findByEmail(request.getEmail()).orElseThrow(()-> new RuntimeException("Student not found"));

        if(!student.getPassword().equals(request.getPassword())){
            throw new RuntimeException("Invalid Password");
        }

        String token= JwtUtil.generateToken(student.getEmail(), student.getRole());

        return new LoginResponse(token);
    }
}
