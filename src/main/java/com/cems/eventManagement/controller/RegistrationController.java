package com.cems.eventManagement.controller;

import com.cems.eventManagement.entity.Event;
import com.cems.eventManagement.entity.Registration;
import com.cems.eventManagement.entity.Student;
import com.cems.eventManagement.services.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @PostMapping("/event/{eventId}")
    public Registration register(@PathVariable Long eventId, Principal principal){
        return registrationService.RegisterStudentForEvent(principal.getName(), eventId);
    }

    @GetMapping("/event/{eventId}")
    public List<Student> getEventsRegistrations(@PathVariable Long eventId){
        return registrationService.getRegistrationByEventsId(eventId);
    }

    @GetMapping("/my-events")
    public List<Event> getStudentRegistrations(Principal principal){
        return registrationService.getRegistrationsByStudentEmail(principal.getName());
    }

    @DeleteMapping("/cancel/event/{eventId}")
    public String cancelRegistration(@PathVariable Long eventId, Principal principal){
        return registrationService.cancelRegistration(principal.getName(), eventId);
    }
}
