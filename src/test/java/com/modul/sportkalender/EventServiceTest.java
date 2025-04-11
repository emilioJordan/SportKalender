package com.modul.sportkalender;

import com.modul.sportkalender.event.Event;
import com.modul.sportkalender.event.EventRepository;
import com.modul.sportkalender.event.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EventServiceTests {

    private EventService eventService;
    private final EventRepository eventRepositoryMock = mock(EventRepository.class);
    private final Event eventMock = mock(Event.class);

    @BeforeEach
    void setUp() {
        eventService = new EventService(eventRepositoryMock);
    }

    @Test
    void testFindAll() {
        when(eventRepositoryMock.findAll()).thenReturn(Collections.singletonList(eventMock));
        List<Event> result = eventService.findAll();
        assertEquals(1, result.size());
        verify(eventRepositoryMock, times(1)).findAll();
    }

    @Test
    void testSave() {
        when(eventRepositoryMock.save(eventMock)).thenReturn(eventMock);
        Event result = eventService.save(eventMock);
        assertNotNull(result);
        verify(eventRepositoryMock, times(1)).save(any());
    }

    @Test
    void testFindById() {
        when(eventRepositoryMock.findById(any())).thenReturn(Optional.of(eventMock));
        Event result = eventService.findById(1L);
        assertNotNull(result);
        verify(eventRepositoryMock, times(1)).findById(any());
    }

    @Test
    void testFindById_NotFound() {
        when(eventRepositoryMock.findById(any())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> eventService.findById(99L));
        verify(eventRepositoryMock, times(1)).findById(99L);
    }

    @Test
    void testUpdateEvent() {
        Event updated = new Event();
        when(eventRepositoryMock.findById(any())).thenReturn(Optional.of(eventMock));
        when(eventRepositoryMock.save(any())).thenReturn(updated);
        Event result = eventService.updateEvent(updated, 1L);
        assertNotNull(result);
        verify(eventRepositoryMock, times(1)).findById(1L);
        verify(eventRepositoryMock, times(1)).save(updated);
    }

    @Test
    void testDelete() {
        doNothing().when(eventRepositoryMock).deleteById(any());
        eventService.delete(1L);
        verify(eventRepositoryMock, times(1)).deleteById(1L);
    }
}
