package com.serhiikovtyka.faiflytesttask.service;

import com.serhiikovtyka.faiflytesttask.dto.*;
import com.serhiikovtyka.faiflytesttask.repository.PatientRepository;
import com.serhiikovtyka.faiflytesttask.repository.VisitRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PatientQueryService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private final PatientRepository patientRepository;
    private final VisitRepository visitRepository;

    public PatientQueryService(PatientRepository patientRepository, VisitRepository visitRepository) {
        this.patientRepository = patientRepository;
        this.visitRepository = visitRepository;
    }

    @Transactional(readOnly = true)
    public PatientListResponse getPatients(Integer page, Integer size, String search, String doctorIdsParam) {
        int p = page == null || page < 0 ? 0 : page;
        int s = size == null || size <= 0 ? 20 : Math.min(size, 200);

        List<PatientRepository.PatientNameProjection> pageData = patientRepository.findAllFiltered(search, PageRequest.of(p, s));
        long total = patientRepository.countFiltered(search);

        List<PatientDto> result = new ArrayList<>(pageData.size());
        Map<Long, PatientDto> byId = new HashMap<>(pageData.size() * 2);

        for (PatientRepository.PatientNameProjection patient : pageData) {
            PatientDto dto = new PatientDto(patient.getFirstName(), patient.getLastName());
            result.add(dto);
            byId.put(patient.getId(), dto);
        }

        if (pageData.isEmpty()) {
            return new PatientListResponse(result, total);
        }

        Set<Long> patientIds = pageData.stream().map(PatientRepository.PatientNameProjection::getId).collect(Collectors.toSet());
        Set<Long> doctorIds = parseDoctorIds(doctorIdsParam);
        boolean doctorIdsEmpty = doctorIds.isEmpty();

        List<VisitRepository.LastVisitProjection> lastVisits = visitRepository.findLastVisitsForPatients(patientIds, doctorIds, doctorIdsEmpty);

        // Compute doctor totals in one query for the doctors present in lastVisits
        Set<Long> doctorsInResult = lastVisits.stream().map(VisitRepository.LastVisitProjection::getDoctorId).collect(Collectors.toSet());
        Map<Long, Long> doctorTotals = doctorsInResult.isEmpty() ? Collections.emptyMap() :
                visitRepository.countDistinctPatientsByDoctorIds(doctorsInResult)
                        .stream().collect(Collectors.toMap(VisitRepository.DoctorPatientsCountProjection::getDoctorId,
                                VisitRepository.DoctorPatientsCountProjection::getTotalPatients));

        for (VisitRepository.LastVisitProjection v : lastVisits) {
            PatientDto dto = byId.get(v.getPatientId());
            if (dto == null) continue;
            ZoneId zone = ZoneId.of(v.getDoctorTimezone());
            String startStr = FMT.format(v.getStartUtc().atZone(zone));
            String endStr = FMT.format(v.getEndUtc().atZone(zone));
            long totalPatients = doctorTotals.getOrDefault(v.getDoctorId(), 0L);
            DoctorDto doctorDto = new DoctorDto(v.getDoctorFirstName(), v.getDoctorLastName(), totalPatients);
            dto.getLastVisits().add(new VisitSummaryDto(startStr, endStr, doctorDto));
        }

        return new PatientListResponse(result, total);
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
