package com.vitality.clinic.service;

import com.vitality.clinic.model.Doctor;
import com.vitality.clinic.repository.DoctorRepository;
import com.vitality.clinic.utils.enums.MedicalSpecialty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public Optional<Doctor> getDoctorById(long id) {
        return doctorRepository.findById(id);
    }

    public List<Doctor> getDoctorsBySpeciality(MedicalSpecialty specialty) {
        return doctorRepository.findAllBySpeciality(specialty);
    }

    @Transactional
    public Optional<Long> addDoctor(Doctor doctor) {
        if (doctor != null)
            return Optional.of(doctorRepository.save(doctor).getId());
        return Optional.empty();
    }
}
