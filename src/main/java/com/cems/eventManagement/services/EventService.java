package com.cems.eventManagement.services;

import com.cems.eventManagement.entity.Event;
import com.cems.eventManagement.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class EventService {

    @Autowired
    private EventRepository eventRepository;


    public List<Event> getAllEvent(){
        return eventRepository.findAll();
    }


    // Get event by ID
    public Event getEventById(Long id) {
        Optional<Event> event = eventRepository.findById(id);
        return event.orElse(null);
    }

    // Create new event
    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    // Update event
//    public Event updateEvent(Long id, Event eventDetails) {
//        Event event = eventRepository.findById(id).orElse(null);
//
//        if (event != null) {
//            event.setEventName(eventDetails.getEventName());
//            event.setDescription(eventDetails.getDescription());
//            event.setEventDate(eventDetails.getEventDate());
//            event.setLocation(eventDetails.getLocation());
//
//            return eventRepository.save(event);
//        }
//
//        return null;
//    }

    // Delete event
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public List<Event> getEventsByDate(String eventDate){
        return eventRepository.findByEventDate(eventDate);
    }
}
