package com.serhiikovtyka.faiflytesttask.dto;

import java.util.List;

public class PatientListResponse {
    private List<PatientDto> data;
    private long count;

    public PatientListResponse() {}

    public PatientListResponse(List<PatientDto> data, long count) {
        this.data = data;
        this.count = count;
    }

    public List<PatientDto> getData() {
        return data;
    }

    public void setData(List<PatientDto> data) {
        this.data = data;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
