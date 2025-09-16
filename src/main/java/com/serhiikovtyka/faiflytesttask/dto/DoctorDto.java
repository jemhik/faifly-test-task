package com.serhiikovtyka.faiflytesttask.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DoctorDto {
    private String firstName;
    private String lastName;
    private long totalPatients;

    public DoctorDto(String firstName, String lastName, long totalPatients) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.totalPatients = totalPatients;
    }
}
