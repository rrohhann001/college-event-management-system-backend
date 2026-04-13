package com.cems.eventManagement.services;

import com.cems.eventManagement.dto.LoginRequest;
import com.cems.eventManagement.dto.LoginResponse;
import com.cems.eventManagement.entity.Student;
import com.cems.eventManagement.repository.StudentRepository;
import com.cems.eventManagement.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request){
        Student student=studentRepository.findByEmail(request.getEmail()).orElseThrow(()-> new RuntimeException("Student not found"));

        if(!passwordEncoder.matches(request.getPassword(),student.getPassword())){
            throw new RuntimeException("Invalid Password");
        }

        String token= jwtUtil.generateToken(student.getEmail(), student.getRole());

        return new LoginResponse(token);
    }
}
