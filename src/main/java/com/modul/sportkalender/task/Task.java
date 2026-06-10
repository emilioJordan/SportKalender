package com.modul.sportkalender.task;

import com.modul.sportkalender.event.Event;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private boolean completed;

    @ManyToOne
    private Event event;

}