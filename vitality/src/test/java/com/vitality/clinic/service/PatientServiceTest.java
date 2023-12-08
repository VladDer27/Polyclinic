package com.vitality.clinic.service;

import com.vitality.clinic.model.Patient;
import com.vitality.clinic.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    private PatientService patientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        patientService = new PatientService(patientRepository);
    }

    @Test
    void testGetPatientById() {
        long id = 1L;
        Patient patient = new Patient();
        patient.setId(id);
        when(patientRepository.findById(id)).thenReturn(Optional.of(patient));

        Optional<Patient> result = patientService.getPatientById(id);

        assertThat(result).isNotEmpty().contains(patient);
    }

    @Test
    void testGetPatientByIdWithInvalidId() {
        long invalidId = -1L;
        when(patientRepository.findById(invalidId)).thenReturn(Optional.empty());

        Optional<Patient> result = patientService.getPatientById(invalidId);

        assertThat(result).isEmpty();
    }

    @Test
    void testAddPatient() {
        Patient patient = new Patient();
        patient.setId(1L);
        when(patientRepository.save(patient)).thenReturn(patient);

        Optional<Long> result = patientService.addPatient(patient);

        assertThat(result).isNotEmpty().contains(patient.getId());
    }

    @Test
    void testAddPatientWithNull() {
        Patient patient = null;

        Optional<Long> result = patientService.addPatient(patient);

        assertThat(result).isEmpty();
    }

}