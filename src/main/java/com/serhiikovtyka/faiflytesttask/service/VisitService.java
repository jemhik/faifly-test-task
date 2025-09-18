package com.serhiikovtyka.faiflytesttask.service;

import com.serhiikovtyka.faiflytesttask.dto.CreateVisitRequest;
import com.serhiikovtyka.faiflytesttask.exception.BadRequestException;
import com.serhiikovtyka.faiflytesttask.exception.NotFoundException;
import com.serhiikovtyka.faiflytesttask.model.Doctor;
import com.serhiikovtyka.faiflytesttask.model.Patient;
import com.serhiikovtyka.faiflytesttask.model.Visit;
import com.serhiikovtyka.faiflytesttask.repository.DoctorRepository;
import com.serhiikovtyka.faiflytesttask.repository.PatientRepository;
import com.serhiikovtyka.faiflytesttask.repository.VisitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;

@Service
public class VisitService {

    private final VisitRepository visitRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public VisitService(VisitRepository visitRepository, PatientRepository patientRepository, DoctorRepository doctorRepository) {
        this.visitRepository = visitRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    @Transactional
    public Long createVisit(CreateVisitRequest req) {
        if (req.getStart() == null || req.getEnd() == null || req.getPatientId() == null || req.getDoctorId() == null) {
            throw new BadRequestException("start, end, patientId and doctorId are required");
        }

        Doctor doctor = doctorRepository.findById(req.getDoctorId())
                .orElseThrow(() -> new NotFoundException("Doctor not found: " + req.getDoctorId()));
        Patient patient = patientRepository.findById(req.getPatientId())
                .orElseThrow(() -> new NotFoundException("Patient not found: " + req.getPatientId()));

        ZoneId doctorZone;
        try {
            doctorZone = ZoneId.of(doctor.getTimezone());
        } catch (DateTimeException ex) {
            throw new BadRequestException("Doctor has invalid timezone: " + doctor.getTimezone());
        }

        LocalDateTime startLocal;
        LocalDateTime endLocal;
        try {
            startLocal = LocalDateTime.parse(req.getStart());
            endLocal = LocalDateTime.parse(req.getEnd());
        } catch (DateTimeException ex) {
            throw new BadRequestException("start/end must be ISO-8601 local date-time, e.g. 2025-09-15T10:00");
        }

        ZonedDateTime startZoned = startLocal.atZone(doctorZone);
        ZonedDateTime endZoned = endLocal.atZone(doctorZone);

        if (!endZoned.isAfter(startZoned)) {
            throw new BadRequestException("end must be after start");
        }

        Instant startUtc = startZoned.toInstant();
        Instant endUtc = endZoned.toInstant();

        boolean doctorOverlap = visitRepository.existsOverlap(doctor.getId(), startUtc, endUtc);
        if (doctorOverlap) {
            throw new BadRequestException("Doctor already has a visit overlapping this time");
        }
        boolean patientOverlap = visitRepository.existsOverlapForPatient(patient.getId(), startUtc, endUtc);
        if (patientOverlap) {
            throw new BadRequestException("Patient already has a visit overlapping this time");
        }

        Visit visit = new Visit(startUtc, endUtc, patient, doctor);
        Visit saved = visitRepository.save(visit);
        return saved.getId();
    }
}
