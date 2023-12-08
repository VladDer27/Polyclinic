package com.vitality.clinic.service;

import com.vitality.clinic.model.Appointment;
import com.vitality.clinic.model.Doctor;
import com.vitality.clinic.model.DoctorSchedule;
import com.vitality.clinic.model.Patient;
import com.vitality.clinic.repository.AppointmentRepository;
import com.vitality.clinic.repository.DoctorRepository;
import com.vitality.clinic.repository.DoctorScheduleRepository;
import com.vitality.clinic.repository.PatientRepository;
import com.vitality.clinic.utils.enums.AppointmentStatus;
import com.vitality.clinic.utils.enums.DayAvailability;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class AppointmentService {
    private final EmailService emailService;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;

    public AppointmentService(EmailService emailService,
                              DoctorRepository doctorRepository,
                              PatientRepository patientRepository,
                              AppointmentRepository appointmentRepository,
                              DoctorScheduleRepository doctorScheduleRepository) {
        this.emailService = emailService;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.doctorScheduleRepository = doctorScheduleRepository;
    }

    public Optional<Appointment> getAppointmentById(long id) {
        return appointmentRepository.findById(id);
    }

    public List<Appointment> getAppointmentsByPatient(Patient patient) {
        return appointmentRepository.findAllByPatient(patient);
    }

    public List<Appointment> getAppointmentsByDoctorAndStatus(Doctor doctor, AppointmentStatus status) {
        return appointmentRepository.findAllByDoctorAndStatus(doctor, status);
    }

    public List<Appointment> getAppointmentsByDoctorAndDateAndStatus(Doctor doctor,
                                                                     LocalDate date, AppointmentStatus status) {
        return appointmentRepository.findAllByDoctorAndDateAndStatus(doctor, date, status);
    }

    @Transactional
    public long addAppointment(
            long doctorId, long patientId, LocalDate date, LocalTime startTime) {

        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(
                () -> new EntityNotFoundException("Doctor not found"));
        Patient patient = patientRepository.findById(patientId).orElseThrow(
                () -> new EntityNotFoundException("Patient not found"));

        Appointment appointment = new Appointment(doctor, patient, date, startTime);

        emailService.appointmentMessage(appointment, AppointmentStatus.ACTIVE);

        appointment.setStatus(AppointmentStatus.ACTIVE);
        doctor.addAppointment(appointment);
        patient.addAppointment(appointment);

        return appointmentRepository.save(appointment).getId();
    }

    @Transactional
    public long updateAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment).getId();
    }

    public List<LocalDate> getDatesOfWeek(int weekFromNow) {
        LocalDate startOfWeek = LocalDate.now().with(DayOfWeek.MONDAY).plusDays(7L * weekFromNow);
        List<LocalDate> datesOfWeek = new ArrayList<>(7);
        for (int i = 0; i < 7; i++)
            datesOfWeek.add(startOfWeek.plusDays(i));
        return datesOfWeek;
    }

    public List<LocalTime> getAvailableTimeForDoctorByDate(Doctor doctor, LocalDate date) {
        Optional<DoctorSchedule> scheduleOptional =
                doctorScheduleRepository.getByDoctorAndDayOfWeek(doctor, date.getDayOfWeek());
        if (scheduleOptional.isEmpty())
            return null;

        DoctorSchedule schedule = scheduleOptional.get();
        LocalTime start = schedule.getWorkdayStart();
        LocalTime end = schedule.getWorkdayEnd();
        int duration = doctor.getAppointmentDuration();

        List<LocalTime> availableTime = new ArrayList<>();
        while (start.isBefore(end)) {
            availableTime.add(start);
            start = start.plusMinutes(duration);
        }

        List<Appointment> appointments =
                appointmentRepository.findAllByDoctorAndDateAndStatus(doctor, date, AppointmentStatus.ACTIVE);
        for (Appointment appointment : appointments) {
            availableTime.remove(appointment.getStartTime());
        }
        return availableTime;
    }

    public Map<Long, Map<LocalDate, DayAvailability>> getAvailabilityMap(List<Doctor> doctors, List<LocalDate> dates){
        Map<Long, Map<LocalDate, DayAvailability>> availabilityMap = new HashMap<>();
        for (Doctor doctor : doctors) {
            Map<LocalDate, DayAvailability> dateAvailabilityMap = new HashMap<>();
            for (LocalDate date : dates) {
                List<LocalTime> availableTime = this.getAvailableTimeForDoctorByDate(doctor, date);
                dateAvailabilityMap.put(date, availableTime == null ? DayAvailability.NOT_IN_SCHEDULE :
                        availableTime.size() == 0 ? DayAvailability.BUSY : DayAvailability.AVAILABLE);
            }
            availabilityMap.put(doctor.getId(), dateAvailabilityMap);
        }
        return availabilityMap;
    }

    public void sortAppointmentsList(List<Appointment> appointments) {
        appointments.sort((a1, a2) -> {
            int dateComparison = a1.getDate().compareTo(a2.getDate());
            if (dateComparison == 0)
                return a1.getStartTime().compareTo(a2.getStartTime());
            else
                return dateComparison;
        });
    }

    public List<LocalDate> getSortedListOfDates(List<Appointment> appointments) {
        Set<LocalDate> dateSet = new HashSet<>();
        for (Appointment appointment : appointments) {
            dateSet.add(appointment.getDate());
        }
        List<LocalDate> dateList = new ArrayList<>(dateSet);
        Collections.sort(dateList);
        return dateList;
    }
}
