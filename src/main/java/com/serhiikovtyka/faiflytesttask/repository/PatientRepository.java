package com.serhiikovtyka.faiflytesttask.repository;

import com.serhiikovtyka.faiflytesttask.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {


    @Query("select count(p) from Patient p where (:search is null or :search = '' or p.firstName like concat('%', :search, '%') or p.lastName like concat('%', :search, '%'))")
    long countFiltered(@Param("search") String search);

    interface PatientPageWithVisitsRow {
        Long getPatientId();
        String getPatientFirstName();
        String getPatientLastName();
        Long getDoctorId();
        Instant getStartUtc();
        Instant getEndUtc();
        String getDoctorFirstName();
        String getDoctorLastName();
        String getDoctorTimezone();
        Long getDoctorTotalPatients();
        Long getTotalCount();
    }

    @Query(value = "WITH filtered_patients AS (\n" +
            "    SELECT p.id, p.first_name, p.last_name\n" +
            "    FROM patient p\n" +
            "    WHERE (:search IS NULL OR :search = '' OR p.first_name LIKE CONCAT('%', :search, '%') OR p.last_name LIKE CONCAT('%', :search, '%'))\n" +
            "), ordered AS (\n" +
            "    SELECT fp.*, ROW_NUMBER() OVER (ORDER BY fp.id) AS rn, COUNT(*) OVER () AS total_count\n" +
            "    FROM filtered_patients fp\n" +
            "), paged_patients AS (\n" +
            "    SELECT * FROM ordered WHERE rn > :offset AND rn <= (:offset + :size)\n" +
            "), lv_ids AS (\n" +
            "    SELECT v.patient_id, v.doctor_id, MAX(v.start_utc) AS start_utc\n" +
            "    FROM visit v\n" +
            "    JOIN paged_patients pp ON pp.id = v.patient_id\n" +
            "    WHERE (:doctorIdsEmpty = TRUE OR v.doctor_id IN (:doctorIds))\n" +
            "    GROUP BY v.patient_id, v.doctor_id\n" +
            "), lv AS (\n" +
            "    SELECT v.patient_id, v.doctor_id, v.start_utc, v.end_utc\n" +
            "    FROM visit v\n" +
            "    JOIN lv_ids lvi ON v.patient_id = lvi.patient_id AND v.doctor_id = lvi.doctor_id AND v.start_utc = lvi.start_utc\n" +
            "), doctor_totals AS (\n" +
            "    SELECT v.doctor_id, COUNT(DISTINCT v.patient_id) AS total_patients\n" +
            "    FROM visit v\n" +
            "    JOIN (SELECT DISTINCT doctor_id FROM lv) dset ON dset.doctor_id = v.doctor_id\n" +
            "    GROUP BY v.doctor_id\n" +
            ")\n" +
            "SELECT pp.id AS patientId, pp.first_name AS patientFirstName, pp.last_name AS patientLastName,\n" +
            "       lv.start_utc AS startUtc, lv.end_utc AS endUtc,\n" +
            "       d.id AS doctorId, d.first_name AS doctorFirstName, d.last_name AS doctorLastName, d.timezone AS doctorTimezone,\n" +
            "       dt.total_patients AS doctorTotalPatients,\n" +
            "       pp.total_count AS totalCount\n" +
            "FROM paged_patients pp\n" +
            "LEFT JOIN lv ON lv.patient_id = pp.id\n" +
            "LEFT JOIN doctor d ON d.id = lv.doctor_id\n" +
            "LEFT JOIN doctor_totals dt ON dt.doctor_id = d.id\n" +
            "ORDER BY pp.id",
            nativeQuery = true)
    List<PatientPageWithVisitsRow> fetchPatientsWithVisitsAndCount(@Param("search") String search,
                                                                   @Param("offset") int offset,
                                                                   @Param("size") int size,
                                                                   @Param("doctorIds") Collection<Long> doctorIds,
                                                                   @Param("doctorIdsEmpty") boolean doctorIdsEmpty);
}
