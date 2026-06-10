package com.modul.sportkalender.task;

import com.modul.sportkalender.task.Task;
import com.modul.sportkalender.task.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/task")
@Validated
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<Task>> all() {
        return new ResponseEntity<>(taskService.findAll(), HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<Task> one(@PathVariable @NonNull Long id) {
        return new ResponseEntity<>(taskService.findById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Task> create(@Valid @RequestBody @NonNull Task task) {
        return new ResponseEntity<>(taskService.save(task), HttpStatus.OK);
    }

    @PutMapping("{id}")
    public ResponseEntity<Task> update(@Valid @RequestBody @NonNull Task task, @PathVariable @NonNull Long id) {
        task.setId(id);
        return new ResponseEntity<>(taskService.save(task), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable @NonNull Long id) {
        taskService.delete(id);
    }
}