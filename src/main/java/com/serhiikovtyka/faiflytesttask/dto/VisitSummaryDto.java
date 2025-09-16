package com.serhiikovtyka.faiflytesttask.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VisitSummaryDto {
    private String start;
    private String end;
    private DoctorDto doctor;

    public VisitSummaryDto(String start, String end, DoctorDto doctor) {
        this.start = start;
        this.end = end;
        this.doctor = doctor;
    }
}
