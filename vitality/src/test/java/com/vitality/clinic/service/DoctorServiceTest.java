package com.vitality.clinic.service;

import com.vitality.clinic.model.Doctor;
import com.vitality.clinic.repository.DoctorRepository;
import com.vitality.clinic.utils.enums.MedicalSpecialty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    private DoctorService doctorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        doctorService = new DoctorService(doctorRepository);
    }

    @Test
    void testGetAllDoctors() {
        List<Doctor> expected = Arrays.asList(new Doctor(), new Doctor());
        when(doctorRepository.findAll()).thenReturn(expected);

        List<Doctor> result = doctorService.getAllDoctors();

        assertThat(result).hasSize(2).containsAll(expected);
    }

    @Test
    void testGetDoctorById() {
        long id = 1L;
        Doctor doctor = new Doctor();
        doctor.setId(id);
        when(doctorRepository.findById(id)).thenReturn(Optional.of(doctor));

        Optional<Doctor> result = doctorService.getDoctorById(id);

        assertThat(result).isNotEmpty().contains(doctor);
    }

    @Test
    void testGetDoctorByIdWithInvalidId() {
        long invalidId = -1L;
        when(doctorRepository.findById(invalidId)).thenReturn(Optional.empty());

        Optional<Doctor> result = doctorService.getDoctorById(invalidId);

        assertThat(result).isEmpty();
    }

    @Test
    void testGetDoctorsBySpeciality() {
        MedicalSpecialty specialty = MedicalSpecialty.CARDIOLOGY;
        List<Doctor> expected = Arrays.asList(new Doctor(), new Doctor());
        when(doctorRepository.findAllBySpeciality(specialty)).thenReturn(expected);

        List<Doctor> result = doctorService.getDoctorsBySpeciality(specialty);

        assertThat(result).hasSize(2).containsAll(expected);
    }

    @Test
    void testAddDoctor() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);
        when(doctorRepository.save(doctor)).thenReturn(doctor);

        Optional<Long> result = doctorService.addDoctor(doctor);

        assertThat(result).isNotEmpty().contains(doctor.getId());
    }

    @Test
    void testAddDoctorWithNull() {
        Doctor doctor = null;

        Optional<Long> result = doctorService.addDoctor(doctor);

        assertThat(result).isEmpty();
    }

}