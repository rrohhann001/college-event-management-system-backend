package com.cems.eventManagement.controller;

import com.cems.eventManagement.dto.ApiResponse;
import com.cems.eventManagement.dto.StudentDto;
import com.cems.eventManagement.entity.Event;
import com.cems.eventManagement.entity.Registration;
import com.cems.eventManagement.entity.Student;
import com.cems.eventManagement.services.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @PostMapping("/event/{eventId}")
    public ResponseEntity<ApiResponse<Registration>> register(@PathVariable Long eventId, Principal principal){
        Registration registration = registrationService.RegisterStudentForEvent(principal.getName(), eventId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Successfully registered for the event", registration));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<ApiResponse<List<StudentDto>>> getEventsRegistrations(@PathVariable Long eventId){
        List<StudentDto> students = registrationService.getRegistrationByEventsId(eventId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Students retrieved for event", students));
    }

    @GetMapping("/my-events")
    public ResponseEntity<ApiResponse<List<Event>>> getStudentRegistrations(Principal principal){
        List<Event> events = registrationService.getRegistrationsByStudentEmail(principal.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Your registered events retrieved, events"));
    }

    @DeleteMapping("/cancel/event/{eventId}")
    public ResponseEntity<ApiResponse<String>> cancelRegistration(@PathVariable Long eventId, Principal principal){
        String msg = registrationService.cancelRegistration(principal.getName(), eventId);
        return ResponseEntity.ok(new ApiResponse<>(true,msg));
    }
}
