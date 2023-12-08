package com.vitality.clinic.repository;

import com.vitality.clinic.model.Doctor;
import com.vitality.clinic.utils.enums.MedicalSpecialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findAllBySpeciality(MedicalSpecialty speciality);
}
