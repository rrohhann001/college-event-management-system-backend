package com.cems.eventManagement.controller;

import com.cems.eventManagement.dto.ApiResponse;
import com.cems.eventManagement.dto.StudentDto;
import com.cems.eventManagement.entity.Student;
import com.cems.eventManagement.repository.StudentRepository;
import com.cems.eventManagement.security.JwtUtil;
import com.cems.eventManagement.services.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

@RestController
@RequestMapping("api/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private JwtUtil jwtUtil;

//    @PostMapping
//    public Student registerStudent(@Valid @RequestBody Student student){
//        return studentService.registerStudent(student);
//    } ye sirf student ko database mai save kar ke student detail return kar dega, without token.


    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> registerStudent(@Valid @RequestBody Student student){

        student.setRole("STUDENT");

        String msg = studentService.initiateRegistration(student);

        if(msg.startsWith("Error")){
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, msg));
        }

        return ResponseEntity.ok(new ApiResponse<>(true, msg));

    }
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyOtp(@RequestParam String email, @RequestParam String otp){

        try {
            Student saveStudent=studentService.verifyOtpAndSave(email, otp);

            String token= jwtUtil.generateToken(saveStudent.getEmail(),saveStudent.getRole());

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("token", token);
            responseData.put("email", saveStudent.getEmail());
            responseData.put("name", saveStudent.getName());

            return ResponseEntity.ok(new ApiResponse<>(true, "Registration Successful!", responseData));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage()));

        }

    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<StudentDto>>> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        Page<StudentDto> studentsPage = studentService.getAllStudents(pageable);

        ApiResponse<Page<StudentDto>> response = new ApiResponse<>(true, "Students retrieved successfully", studentsPage);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentDto>> getStudentById(@PathVariable Long id){
        StudentDto student = studentService.getStudentById(id);
        return ResponseEntity.ok(new ApiResponse<>(true,"Student found", student));
    }

    @GetMapping("/course/{courseName}")
    public ResponseEntity<ApiResponse<List<StudentDto>>> getStudentsByCourse(@PathVariable String courseName){
        List<StudentDto> students  = studentService.getStudentsByCourse(courseName);

        return ResponseEntity.ok(new ApiResponse<>(true, "Students retrieved for course", students));
    }

    @GetMapping("/my-profile")
    public ResponseEntity<ApiResponse<StudentDto>> getMyProfile(Principal principal) {
        String loggedInEmail = principal.getName();

        Student student = studentRepository.findByEmail(loggedInEmail)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        StudentDto studentDto = studentService.convertToDto(student);

        return ResponseEntity.ok(new ApiResponse<>(true, "Profile loaded successfully", studentDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or @studentService.isOwnProfile(#id, authentication.name)")
    public ResponseEntity<ApiResponse<String>> deleteStudent(@PathVariable Long id, Principal principal){
        studentService.deleteStudent(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Student with ID " + id + " has been deleted."));
    }
}
