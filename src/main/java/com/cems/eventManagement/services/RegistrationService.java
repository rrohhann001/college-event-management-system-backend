package com.cems.eventManagement.services;

import com.cems.eventManagement.entity.Event;
import com.cems.eventManagement.entity.Registration;
import com.cems.eventManagement.entity.Student;
import com.cems.eventManagement.repository.EventRepository;
import com.cems.eventManagement.repository.RegistrationRepository;
import com.cems.eventManagement.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private StudentRepository studentRepository;

    public Registration RegisterStudentForEvent(String email, Long eventId){
        Student student=studentRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("Student not found"));

        Event event=eventRepository.findById(eventId).orElseThrow(()->new RuntimeException("Event not found"));

        // 1. Check karein ki student pehle se toh registered nahi hai
        Optional<Registration> existingRegistration=registrationRepository.findByStudentIdAndEventId(student.getId(),eventId);
        if(existingRegistration.isPresent()){
            throw new RuntimeException("Student already registered in this event");
        }

        //CAPACITY CHECK
        long registrationCount = registrationRepository.countByEventId(eventId);
        if(registrationCount >= event.getCapacity()){
            throw new RuntimeException("Event is full, Cannot register");
        }

        // 3. Agar sab theek hai toh register kar dein
        Registration registration=new Registration();
        registration.setStudent(student);
        registration.setEvent(event);

        return registrationRepository.save(registration);


    }

    // 1. Ek Event me kitne students hain, unki list lana
    public List<Student> getRegistrationByEventsId(Long eventId){

        //return registrationRepository.findByEventId(eventId); ish se har baar event ke saath student details bhi aa rahi thi
        List<Registration> registrations=registrationRepository.findByEventId(eventId);

        List<Student> students= new ArrayList<>();
        for (Registration r:registrations){
            students.add(r.getStudent());
        }
        return students;
    }

    //Ek Student kis-kis event me hai, uski list lana
    public List<Event> getRegistrationsByStudentEmail(String email){

        // 1. Pehle email se Student dhoondho
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("Student not found"));

        //return registrationRepository.findByStudentId(studentId); ish se vo ek student(details) har baar events details ke saath show hongi
        List<Registration> registrations=registrationRepository.findByStudentId(student.getId());

        List<Event> events=new ArrayList<>();
        for(Registration r:registrations){
            events.add(r.getEvent());
        }
        return events;
    }

    public String cancelRegistration(String email, Long eventId){

        Student student = studentRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("Student not found"));

        Optional<Registration> registrationOpt=registrationRepository.findByStudentIdAndEventId(student.getId(), eventId);

        if(registrationOpt.isPresent()){
            Registration registration=registrationOpt.get();
            registrationRepository.delete(registration);

            return "Success: Registration Canceled";
        }
        else {
            return "Error: Registration Not Found!";
        }
    }


}
