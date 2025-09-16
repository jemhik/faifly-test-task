package com.serhiikovtyka.faiflytesttask.repository;

import com.serhiikovtyka.faiflytesttask.model.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, Long> {

    @Query("select case when count(v) > 0 then true else false end from Visit v where v.doctor.id = :doctorId and v.startUtc < :end and v.endUtc > :start")
    boolean existsOverlap(@Param("doctorId") Long doctorId,
                          @Param("start") Instant start,
                          @Param("end") Instant end);

    interface LastVisitProjection {
        Long getPatientId();
        Long getDoctorId();
        Instant getStartUtc();
        Instant getEndUtc();
        String getDoctorFirstName();
        String getDoctorLastName();
        String getDoctorTimezone();
    }

    // MySQL 8 window function to get last visit per (patient, doctor)
    @Query(value = "SELECT x.patient_id AS patientId, x.doctor_id AS doctorId, x.start_utc AS startUtc, x.end_utc AS endUtc, d.first_name AS doctorFirstName, d.last_name AS doctorLastName, d.timezone AS doctorTimezone\n" +
            "FROM (\n" +
            "  SELECT v.*, ROW_NUMBER() OVER(PARTITION BY v.patient_id, v.doctor_id ORDER BY v.start_utc DESC) rn\n" +
            "  FROM visit v\n" +
            "  WHERE v.patient_id IN (:patientIds)\n" +
            "    AND (:doctorIdsEmpty = true OR v.doctor_id IN (:doctorIds))\n" +
            ") x\n" +
            "JOIN doctor d ON d.id = x.doctor_id\n" +
            "WHERE x.rn = 1",
            nativeQuery = true)
    List<LastVisitProjection> findLastVisitsForPatients(@Param("patientIds") Collection<Long> patientIds,
                                                        @Param("doctorIds") Collection<Long> doctorIds,
                                                        @Param("doctorIdsEmpty") boolean doctorIdsEmpty);

    interface DoctorPatientsCountProjection {
        Long getDoctorId();
        Long getTotalPatients();
    }

    @Query(value = "SELECT v.doctor_id AS doctorId, COUNT(DISTINCT v.patient_id) AS totalPatients FROM visit v WHERE v.doctor_id IN (:doctorIds) GROUP BY v.doctor_id",
            nativeQuery = true)
    List<DoctorPatientsCountProjection> countDistinctPatientsByDoctorIds(@Param("doctorIds") Collection<Long> doctorIds);
}
