package com.serhiikovtyka.faiflytesttask.service;

import com.serhiikovtyka.faiflytesttask.dto.*;
import com.serhiikovtyka.faiflytesttask.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class PatientQueryService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private final PatientRepository patientRepository;

    public PatientQueryService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Transactional(readOnly = true)
    public PatientListResponse getPatients(Integer page, Integer size, String search, String doctorIdsParam) {
        int p = page == null || page < 0 ? 0 : page;
        int s = size == null || size <= 0 ? 20 : Math.min(size, 200);
        int offset = p * s;

        Set<Long> doctorIds = parseDoctorIds(doctorIdsParam);
        boolean doctorIdsEmpty = doctorIds.isEmpty();

        List<PatientRepository.PatientPageWithVisitsRow> rows = patientRepository.fetchPatientsWithVisitsAndCount(
                search, offset, s, doctorIds, doctorIdsEmpty
        );

        Map<Long, PatientDto> byId = new LinkedHashMap<>();
        long total = 0L;

        for (PatientRepository.PatientPageWithVisitsRow r : rows) {
            total = r.getTotalCount() == null ? total : r.getTotalCount();
            PatientDto dto = byId.computeIfAbsent(r.getPatientId(), id -> new PatientDto(r.getPatientFirstName(), r.getPatientLastName()));
            if (r.getDoctorId() != null && r.getStartUtc() != null && r.getEndUtc() != null) {
                ZoneId zone = ZoneId.of(r.getDoctorTimezone());
                String startStr = FMT.format(r.getStartUtc().atZone(zone));
                String endStr = FMT.format(r.getEndUtc().atZone(zone));
                long totalPatients = r.getDoctorTotalPatients() == null ? 0L : r.getDoctorTotalPatients();
                DoctorDto doctorDto = new DoctorDto(r.getDoctorFirstName(), r.getDoctorLastName(), totalPatients);
                dto.getLastVisits().add(new VisitSummaryDto(startStr, endStr, doctorDto));
            }
        }

        // Fallback to count when page has no rows (e.g., page beyond last)
        if (rows.isEmpty()) {
            total = patientRepository.countFiltered(search);
        }

        return new PatientListResponse(new ArrayList<>(byId.values()), total);
    }

    private Set<Long> parseDoctorIds(String doctorIdsParam) {
        if (doctorIdsParam == null || doctorIdsParam.trim().isEmpty()) return Collections.emptySet();
        String[] parts = doctorIdsParam.split(",");
        Set<Long> ids = new HashSet<>();
        for (String p : parts) {
            try {
                ids.add(Long.parseLong(p.trim()));
            } catch (NumberFormatException ignored) {}
        }
        return ids;
    }
}
