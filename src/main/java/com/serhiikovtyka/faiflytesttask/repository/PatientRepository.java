package com.serhiikovtyka.faiflytesttask.repository;

import com.serhiikovtyka.faiflytesttask.model.Patient;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    interface PatientNameProjection {
        Long getId();
        String getFirstName();
        String getLastName();
    }

    // Return only required columns and avoid implicit count by returning List with Pageable
    @Query("select p.id as id, p.firstName as firstName, p.lastName as lastName from Patient p " +
           "where (:search is null or :search = '' or p.firstName like concat('%', :search, '%') or p.lastName like concat('%', :search, '%'))")
    List<PatientNameProjection> findAllFiltered(@Param("search") String search, Pageable pageable);

    @Query("select count(p) from Patient p where (:search is null or :search = '' or p.firstName like concat('%', :search, '%') or p.lastName like concat('%', :search, '%'))")
    long countFiltered(@Param("search") String search);
}
