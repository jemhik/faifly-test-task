package com.serhiikovtyka.faiflytesttask.controller;

import com.serhiikovtyka.faiflytesttask.dto.CreateVisitRequest;
import com.serhiikovtyka.faiflytesttask.service.VisitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/visits")
public class VisitController {

    private final VisitService visitService;

    public VisitController(VisitService visitService) {
        this.visitService = visitService;
    }

    @PostMapping
    public ResponseEntity<?> createVisit(@RequestBody CreateVisitRequest request) {
        Long id = visitService.createVisit(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreatedResponse(id));
    }

    static class CreatedResponse {
        public Long id;
        public CreatedResponse(Long id) { this.id = id; }
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }
}
