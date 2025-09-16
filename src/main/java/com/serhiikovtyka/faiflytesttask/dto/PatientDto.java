package com.serhiikovtyka.faiflytesttask.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PatientDto {
    private String firstName;
    private String lastName;
    private List<VisitSummaryDto> lastVisits = new ArrayList<>();

    public PatientDto(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
