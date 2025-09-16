package com.serhiikovtyka.faiflytesttask.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "visit",
       indexes = {
           @Index(name = "idx_visit_doctor_time", columnList = "doctor_id,start_utc,end_utc"),
           @Index(name = "idx_visit_patient", columnList = "patient_id"),
           @Index(name = "idx_visit_patient_doctor_start", columnList = "patient_id,doctor_id,start_utc")
       })
public class Visit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_utc", nullable = false)
    private Instant startUtc;

    @Column(name = "end_utc", nullable = false)
    private Instant endUtc;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    public Visit() {}

    public Visit(Instant startUtc, Instant endUtc, Patient patient, Doctor doctor) {
        this.startUtc = startUtc;
        this.endUtc = endUtc;
        this.patient = patient;
        this.doctor = doctor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStartUtc() {
        return startUtc;
    }

    public void setStartUtc(Instant startUtc) {
        this.startUtc = startUtc;
    }

    public Instant getEndUtc() {
        return endUtc;
    }

    public void setEndUtc(Instant endUtc) {
        this.endUtc = endUtc;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }
}
