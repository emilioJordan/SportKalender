package com.modul.sportkalender.event;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    public Event save(Event event) {
        return eventRepository.save(event);
    }

    public void delete(Long id) {
        eventRepository.deleteById(id);
    }

    public Event updateEvent(Event updatedEvent, Long id) {
        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        updatedEvent.setId(existingEvent.getId());
        return eventRepository.save(updatedEvent);
    }

    public Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
    }

}
