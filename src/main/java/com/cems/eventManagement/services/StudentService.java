package com.cems.eventManagement.services;

import com.cems.eventManagement.dto.StudentDto;
import com.cems.eventManagement.entity.Student;
import com.cems.eventManagement.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EmailService emailService;

    private Map<String,PendingStudent> waitingRoom = new ConcurrentHashMap<>();


    public String initiateRegistration(Student student){
        if(studentRepository.existsByEmail(student.getEmail())){
            return "Error: This email is already registered";
        }

        student.setRole("Student");

        String generatedOtp = String.format("%06d", new Random().nextInt(999999));

        waitingRoom.put(student.getEmail(), new PendingStudent(student, generatedOtp, System.currentTimeMillis()));
        emailService.sendOtpMail(student.getEmail(), generatedOtp);

        return "Success: The otp has been sent to your email. Please verify within 30 seconds.";
    }


    public Student verifyOtpAndSave(String email, String otp) throws Exception{

        if(!waitingRoom.containsKey(email)){
            throw new Exception("Error: The session has expired or the email address is incorrect. Please register again.");
        }

        PendingStudent pendingStudent = waitingRoom.get(email);

        long currentTime = System.currentTimeMillis();
        long elapcedTime = currentTime - pendingStudent.timestamp;

        if(elapcedTime>=60000){
            waitingRoom.remove(email);
            throw new Exception("Error: OTP Expired! Please register again") ;
        }

        if(!pendingStudent.otp.equals(otp)){
            throw new Exception("Error: Wrong OTP!");
        }

        Student studentToSave=pendingStudent.student;
        studentToSave.setVerified(true);
        studentToSave.setOtp(otp);

        Student savedStudent = studentRepository.save(studentToSave);
        waitingRoom.remove(email);

        return savedStudent;


    }
//    public Student registerStudent(Student student){
//
//        String generatedOtp = String.format("%06d", new Random().nextInt(999999));
//
//        student.setOtp(generatedOtp);
//        student.setVerified(false);
//
//        Student savedStudent = studentRepository.save(student);
//
//        emailService.sendOtpMail(savedStudent.getEmail(), generatedOtp);
//
//        return savedStudent;
//    }


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



    public static class PendingStudent{
        Student student;
        String otp;
        long timestamp;

        public PendingStudent (Student student, String otp, long timestamp){
            this.student=student;
            this.otp=otp;
            this.timestamp=timestamp;
        }
    }
}
