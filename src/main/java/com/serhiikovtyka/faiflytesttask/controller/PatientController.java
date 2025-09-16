package com.serhiikovtyka.faiflytesttask.controller;

import com.serhiikovtyka.faiflytesttask.dto.PatientListResponse;
import com.serhiikovtyka.faiflytesttask.service.PatientQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientQueryService patientQueryService;

    public PatientController(PatientQueryService patientQueryService) {
        this.patientQueryService = patientQueryService;
    }

    @GetMapping
    public PatientListResponse listPatients(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "doctorIds", required = false) String doctorIds
    ) {
        return patientQueryService.getPatients(page, size, search, doctorIds);
    }
}
