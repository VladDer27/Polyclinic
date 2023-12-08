package com.vitality.clinic.repository;

import com.vitality.clinic.model.Appointment;
import com.vitality.clinic.model.Doctor;
import com.vitality.clinic.model.Patient;
import com.vitality.clinic.utils.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findAllByDoctorAndDateAndStatus(Doctor doctor, LocalDate date, AppointmentStatus status);
    List<Appointment> findAllByDoctorAndStatus(Doctor doctor, AppointmentStatus status);
    List<Appointment> findAllByPatient(Patient patient);
    void deleteAllByDoctorAndStatus(Doctor doctor, AppointmentStatus status);
}
