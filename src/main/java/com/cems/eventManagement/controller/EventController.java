package com.cems.eventManagement.controller;

import com.cems.eventManagement.entity.Event;
import com.cems.eventManagement.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Event> getAllEvents() {
        return eventService.getAllEvent();
    }

    // Get event by ID
    // GET: http://localhost:8080/api/events/1
    @GetMapping("/{id}")
    public Event getEventById(@PathVariable Long id) {
        return eventService.getEventById(id);
    }

    @GetMapping("/date/{eventDate}")
     public List<Event> getEventsByDate(@PathVariable String eventDate){
        return eventService.getEventsByDate(eventDate);
    }

    // Create event
    // POST: http://localhost:8080/api/events
    @PostMapping
    public Event createEvent(@RequestBody Event event) {
        return eventService.createEvent(event);
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
    public void deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
    }
}
