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

    @Query("select case when count(v) > 0 then true else false end from Visit v where v.patient.id = :patientId and v.startUtc < :end and v.endUtc > :start")
    boolean existsOverlapForPatient(@Param("patientId") Long patientId,
                                    @Param("start") Instant start,
                                    @Param("end") Instant end);

}
