package com.cems.eventManagement.controller;

import com.cems.eventManagement.dto.ApiResponse;
import com.cems.eventManagement.entity.Event;
import com.cems.eventManagement.services.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {


    @Autowired
    private EventService eventService;

    // Get all events
    // GET: http://localhost:8080/api/events
    @GetMapping
    public ResponseEntity<ApiResponse<List<Event>>> getAllEvents() {
        List<Event> events =  eventService.getAllEvent();
        return ResponseEntity.ok(new ApiResponse<>(true,"All events retrieved successfully", events));
    }

    // Get event by ID
    // GET: http://localhost:8080/api/events/1
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Event>> getEventById(@PathVariable Long id) {
        Event event = eventService.getEventById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Event details retrieved", event));
    }

    @GetMapping("/date/{eventDate}")
     public ResponseEntity<ApiResponse<List<Event>>> getEventsByDate(@PathVariable String eventDate){
        List<Event> events = eventService.getEventsByDate(eventDate);
        return ResponseEntity.ok(new ApiResponse<>(true,"Event details retrieved", events));
    }

    // Create event
    // POST: http://localhost:8080/api/events
    @PostMapping
    public ResponseEntity<ApiResponse<Event>> createEvent(@Valid @RequestBody Event eventDate) {
        Event createdEvent = eventService.createEvent(eventDate);
        return ResponseEntity.ok(new ApiResponse<>(true, "Event created successfully", createdEvent));
    }

    // Update event
    // PUT: http://localhost:8080/api/events/1
//    @PutMapping("/{id}")
//    public Event updateEvent(@PathVariable Long id, @RequestBody Event event) {
//        return eventService.updateEvent(id, event);
//    }

    // Delete event
    // DELETE: http://localhost:8080/api/events/1
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Event>> deleteEvent(@PathVariable Long id) {
       eventService.deleteEvent(id);
       return ResponseEntity.ok(new ApiResponse<>(true,"Event with ID " + id + " has been deleted successfully"));
    }
}
