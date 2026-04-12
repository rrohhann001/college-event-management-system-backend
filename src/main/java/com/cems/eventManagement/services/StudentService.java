package com.cems.eventManagement.services;

import com.cems.eventManagement.dto.StudentDto;
import com.cems.eventManagement.entity.Student;
import com.cems.eventManagement.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final long OTP_Expiration_Time=60000;

    private Map<String,PendingStudent> waitingRoom = new ConcurrentHashMap<>();


    public String initiateRegistration(Student student){
        log.info("Registration initiated for email: {}", student.getEmail());
        if(studentRepository.existsByEmail(student.getEmail())){
            log.warn("Duplicate registration attempt for email: {}", student.getEmail());
            return "Error: This email is already registered";
        }

        student.setRole("STUDENT");

        String encryptPassword = passwordEncoder.encode(student.getPassword());
        student.setPassword(encryptPassword);

        String generatedOtp = String.format("%06d", new Random().nextInt(999999));

        waitingRoom.put(student.getEmail(), new PendingStudent(student, generatedOtp, System.currentTimeMillis()));
        emailService.sendOtpMail(student.getEmail(), generatedOtp);

        return "Success: The otp has been sent to your email. Please verify within 1 minute.";
    }


    public Student verifyOtpAndSave(String email, String otp) throws Exception{

        if(!waitingRoom.containsKey(email)){
            throw new Exception("Error: The session has expired or the email address is incorrect. Please register again.");
        }

        PendingStudent pendingStudent = waitingRoom.get(email);

        long currentTime = System.currentTimeMillis();
        long elapcedTime = currentTime - pendingStudent.timestamp;

        if(elapcedTime>=OTP_Expiration_Time){
            waitingRoom.remove(email);
            throw new Exception("Error: OTP Expired! Please register again") ;
        }

        if(!pendingStudent.otp.equals(otp)){
            throw new Exception("Error: Wrong OTP!");
        }

        Student studentToSave=pendingStudent.student;
        studentToSave.setVerified(true);

        Student savedStudent = studentRepository.save(studentToSave);
        waitingRoom.remove(email);

        return savedStudent;


    }

    @Scheduled(fixedRate = 120000)
    public void cleanupExpiredOtp(){
        long currentTime = System.currentTimeMillis();
        waitingRoom.entrySet().removeIf(entry ->
                currentTime - entry.getValue().timestamp > OTP_Expiration_Time
        );
    }

    private StudentDto convertToDto(Student student){

        StudentDto dto =new StudentDto();
        dto.setId(student.getId());
        dto.setName(student.getName());
        dto.setEmail(student.getEmail());
        dto.setCourse(student.getCourse());
        dto.setRollNumber(student.getRollNumber());

        return dto;
    }


    public Page<StudentDto> getAllStudents(Pageable pageable){
        Page<Student> studentPage = studentRepository.findAll(pageable);

        return studentPage.map(this::convertToDto);
        //pehle ye sab alag alag krana padta tha lekin method banane ke baad bss itna karna padta hai

//        List<Student> students = studentRepository.findAll();
//        List<StudentDto> studentDtos=new ArrayList<>();
//        for(Student student:students){
//
//            StudentDto dto=new StudentDto();
//            dto.setId(student.getId());
//            dto.setName(student.getName());
//            dto.setCourse(student.getCourse());
//            dto.setEmail(student.getEmail());
//            dto.setRollNumber(student.getRollNumber());
//
//            studentDtos.add(dto);
//
//        }
//        return studentDtos;
    }


    public StudentDto getStudentById(Long id){
        Student student = studentRepository.findById(id).orElseThrow(()-> new RuntimeException("Student not found"));

//        StudentDto dto=new StudentDto();
//        dto.setId(student.getId());
//        dto.setName(student.getName());r5r
//        dto.setCourse(student.getCourse());
//        dto.setEmail(student.getEmail());
//        dto.setRollNumber(student.getRollNumber());
//        return dto;

        return convertToDto(student);

    }


    public void deleteStudent(Long id){
        studentRepository.deleteById(id);
    }


    public List<StudentDto> getStudentsByCourse(String course) {
        return studentRepository.findStudentsByCourse(course)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    public boolean isOwnProfile(Long studentId, String email){
        Student student = studentRepository.findById(studentId).orElse(null);
        return student != null && student.getEmail().equals(email);
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
