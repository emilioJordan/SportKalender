package com.modul.sportkalender.event;

import com.modul.sportkalender.event.Event;
import com.modul.sportkalender.event.EventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/event")
@Validated
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<List<Event>> all() {
        return new ResponseEntity<>(eventService.findAll(), HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<Event> one(@PathVariable @NonNull Long id) {
        return new ResponseEntity<>(eventService.findById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Event> create(@Valid @RequestBody @NonNull Event event) {
        return new ResponseEntity<>(eventService.save(event), HttpStatus.OK);
    }

    @PutMapping("{id}")
    public ResponseEntity<Event> update(@Valid @RequestBody @NonNull Event event, @PathVariable @NonNull Long id) {
        event.setId(id);
        return new ResponseEntity<>(eventService.save(event), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable @NonNull Long id) {
        eventService.delete(id);
    }
}