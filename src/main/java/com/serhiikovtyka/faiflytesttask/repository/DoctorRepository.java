package com.serhiikovtyka.faiflytesttask.repository;

import com.serhiikovtyka.faiflytesttask.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}
