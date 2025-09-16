package com.serhiikovtyka.faiflytesttask.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateVisitRequest {
    private String start; // ISO-8601 local date-time in doctor's timezone
    private String end;   // ISO-8601 local date-time in doctor's timezone
    private Long patientId;
    private Long doctorId;
}
