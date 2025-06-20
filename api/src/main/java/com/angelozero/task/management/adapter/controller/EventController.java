package com.angelozero.task.management.adapter.controller;

import com.angelozero.task.management.adapter.controller.mapper.EventRequestMapper;
import com.angelozero.task.management.adapter.controller.rest.request.EventRequest;
import com.angelozero.task.management.adapter.controller.rest.response.EventResponse;
import com.angelozero.task.management.usecase.services.event.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/event")
@AllArgsConstructor
public class EventController {

    private final EventPublisherUseCase eventPublisherUseCase;
    private final GetEventById getEventById;
    private final GetEventByPersonEmail getEventByPersonEmail;
    private final ReadEventByEventPersonEmailUseCase readEventByEventPersonEmailUseCase;
    private final ReadEventByEventIdUseCase readEventByEventIdUseCase;
    private final EventRequestMapper eventRequestMapper;

    @PostMapping
    public ResponseEntity<Void> saveEvent(@RequestBody EventRequest eventRequest) {
        var taskId = eventRequest.taskId();
        var email = eventRequest.email();
        var message = eventRequest.message();

        eventPublisherUseCase.execute(taskId, email, message);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable Integer id) {
        var event = getEventById.execute(id);
        var eventResponse = eventRequestMapper.toEventResponse(event);

        return ResponseEntity.ok(eventResponse);
    }

    @GetMapping()
    public ResponseEntity<EventResponse> getEventByPersonEmail(@RequestParam String email) {
        var event = getEventByPersonEmail.execute(email);
        var eventResponse = eventRequestMapper.toEventResponse(event);

        return ResponseEntity.ok(eventResponse);
    }

    @PutMapping()
    public ResponseEntity<Void> updateReadStatus(@RequestParam(required = false) Integer eventId,
                                                 @RequestParam(required = false) String eventPersonEmail,
                                                 @RequestParam boolean isRead) {
        if (eventId != null) {
            readEventByEventIdUseCase.execute(eventId, isRead);

        } else if (eventPersonEmail != null && !eventPersonEmail.isBlank()) {
            readEventByEventPersonEmailUseCase.execute(eventPersonEmail, isRead);

        } else {
            return ResponseEntity.badRequest().header("error message",
                    "the values \"event id\" or \"event id\" should not be null or empty").build();
        }
        return ResponseEntity.noContent().build();
    }
}
