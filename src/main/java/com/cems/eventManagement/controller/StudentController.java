package com.cems.eventManagement.controller;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<Map<String, String>> registerStudent(@Valid @RequestBody Student student){

        student.setRole("STUDENT");

        String msg = studentService.initiateRegistration(student);

        Map<String, String> response=new HashMap<>();
        response.put("Message", msg);

        if(msg.startsWith("Error")){
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);

    }
    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestParam String email, @RequestParam String otp){

        try {
            Student saveStudent=studentService.verifyOtpAndSave(email, otp);

            String token= jwtUtil.generateToken(saveStudent.getEmail(),saveStudent.getRole());

            Map<String, Object> response = new HashMap<>();
            response.put("Message", "Registration Successful !");
            response.put("token", token);
            response.put("email", saveStudent.getEmail());
            response.put("name", saveStudent.getName());

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("Error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);

        }

    }

    @GetMapping
    public ResponseEntity<Page<StudentDto>> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        Page<StudentDto> studentsPage = studentService.getAllStudents(pageable);
        return ResponseEntity.ok(studentsPage);
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
    @PreAuthorize("hasAuthority('ADMIN') or @studentService.isOwnProfile(#id, authentication.name)")
    public String deleteStudent(@PathVariable Long id, Principal principal){
        studentService.deleteStudent(id);
        return "Student with ID " + id + " has been deleted.";
    }
}
