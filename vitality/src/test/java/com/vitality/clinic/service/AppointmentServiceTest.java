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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
class AppointmentServiceTest {
    @Mock
    private EmailService emailService;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorScheduleRepository doctorScheduleRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        appointmentService = new AppointmentService(emailService, doctorRepository, patientRepository, appointmentRepository, doctorScheduleRepository);
    }

    @Test
    public void testGetAppointmentById() {
        Appointment appointment = new Appointment();
        Mockito.when(appointmentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(appointment));

        Optional<Appointment> result = appointmentService.getAppointmentById(1L);
        assertTrue(result.isPresent());
        assertEquals(appointment, result.get());
    }

    @Test
    public void testGetAppointmentsByPatient() {
        Patient patient = new Patient();
        List<Appointment> appointments = new ArrayList<>();
        Mockito.when(appointmentRepository.findAllByPatient(Mockito.any())).thenReturn(appointments);

        List<Appointment> result = appointmentService.getAppointmentsByPatient(patient);
        assertEquals(appointments, result);
    }

    @Test
    public void testGetAppointmentsByDoctorAndStatus() {
        Doctor doctor = new Doctor();
        List<Appointment> appointments = new ArrayList<>();
        Mockito.when(appointmentRepository.findAllByDoctorAndStatus(Mockito.any(), Mockito.any())).thenReturn(appointments);

        List<Appointment> result = appointmentService.getAppointmentsByDoctorAndStatus(doctor, AppointmentStatus.ACTIVE);
        assertEquals(appointments, result);
    }

    @Test
    public void testGetAppointmentsByDoctorAndDateAndStatus() {
        Doctor doctor = new Doctor();
        LocalDate date = LocalDate.now();
        List<Appointment> appointments = new ArrayList<>();
        Mockito.when(appointmentRepository.findAllByDoctorAndDateAndStatus(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(appointments);

        List<Appointment> result = appointmentService.getAppointmentsByDoctorAndDateAndStatus(doctor, date, AppointmentStatus.ACTIVE);
        assertEquals(appointments, result);
    }

    @Test
    public void testAddAppointment() {
        Doctor doctor = new Doctor();
        doctor.setAppointments(new ArrayList<>());
        Patient patient = new Patient();
        patient.setAppointments(new ArrayList<>());
        Appointment appointment = new Appointment(doctor, patient, LocalDate.now(), LocalTime.now());

        Mockito.when(doctorRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(doctor));
        Mockito.when(patientRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(patient));
        Mockito.when(appointmentRepository.save(Mockito.any())).thenReturn(appointment);

        long id = appointmentService.addAppointment(1L, 2L, LocalDate.now(), LocalTime.now());
        assertEquals(appointment.getId(), id);
    }

    @Test
    public void testUpdateAppointment() {
        Appointment appointment = new Appointment();
        appointment.setId(1L);

        Mockito.when(appointmentRepository.save(Mockito.any())).thenReturn(appointment);

        long result = appointmentService.updateAppointment(appointment);

        assertEquals(appointment.getId(), result);
        Mockito.verify(appointmentRepository).save(appointment);
    }

    @Test
    public void testGetDatesOfWeek() {
        LocalDate now = LocalDate.now();
        LocalDate expectedStart = now.with(DayOfWeek.MONDAY).plusDays(7L * 2);
        List<LocalDate> expectedDates = new ArrayList<>(7);
        for (int i = 0; i < 7; i++)
            expectedDates.add(expectedStart.plusDays(i));

        List<LocalDate> result = appointmentService.getDatesOfWeek(2);

        assertEquals(expectedDates, result);
    }

    @Test
    public void testGetAvailableTimeForDoctorByDate() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setAppointmentDuration(60);

        DoctorSchedule schedule = new DoctorSchedule();
        schedule.setWorkdayStart(LocalTime.of(9, 0));
        schedule.setWorkdayEnd(LocalTime.of(15, 0));

        List<Appointment> appointments = new ArrayList<>();
        Appointment appointment = new Appointment();
        appointment.setStartTime(LocalTime.of(9, 0));
        appointment.setStatus(AppointmentStatus.ACTIVE);
        appointments.add(appointment);

        Mockito.when(doctorScheduleRepository.getByDoctorAndDayOfWeek(Mockito.any(), Mockito.any()))
                .thenReturn(Optional.of(schedule));
        Mockito.when(appointmentRepository.findAllByDoctorAndDateAndStatus(Mockito.any(), Mockito.any(),
                Mockito.any())).thenReturn(appointments);

        LocalDate date = LocalDate.now();
        List<LocalTime> expectedTime = Arrays.asList(LocalTime.of(10, 0), LocalTime.of(11, 0),
                LocalTime.of(12, 0), LocalTime.of(13, 0), LocalTime.of(14, 0));
        List<LocalTime> result = appointmentService.getAvailableTimeForDoctorByDate(doctor, date);

        assertEquals(expectedTime, result);
    }

    @Test
    public void testGetAvailabilityMap() {
        Doctor doctor1 = new Doctor();
        doctor1.setId(1L);
        Doctor doctor2 = new Doctor();
        doctor2.setId(2L);
        List<Doctor> doctors = Arrays.asList(doctor1, doctor2);
        LocalDate now = LocalDate.now();
        LocalDate date1 = now.plusDays(1);
        LocalDate date2 = now.plusDays(2);
        List<LocalDate> dates = Arrays.asList(now, date1, date2);

        DoctorSchedule schedule1 = new DoctorSchedule();
        schedule1.setDoctor(doctor1);
        schedule1.setDayOfWeek(DayOfWeek.MONDAY);
        schedule1.setWorkdayStart(LocalTime.of(8, 0));
        schedule1.setWorkdayEnd(LocalTime.of(16, 0));
        Mockito.when(doctorScheduleRepository.getByDoctorAndDayOfWeek(doctor1, DayOfWeek.MONDAY))
                .thenReturn(Optional.of(schedule1));

        DoctorSchedule schedule2 = new DoctorSchedule();
        schedule2.setDoctor(doctor2);
        schedule2.setDayOfWeek(DayOfWeek.MONDAY);
        schedule2.setWorkdayStart(LocalTime.of(10, 0));
        schedule2.setWorkdayEnd(LocalTime.of(18, 0));
        Mockito.when(doctorScheduleRepository.getByDoctorAndDayOfWeek(doctor2, DayOfWeek.MONDAY))
                .thenReturn(Optional.of(schedule2));

        Appointment appointment1 = new Appointment();
        appointment1.setId(1L);
        appointment1.setDoctor(doctor1);
        appointment1.setDate(date1);
        appointment1.setStartTime(LocalTime.of(8, 0));
        appointment1.setStatus(AppointmentStatus.ACTIVE);

        Appointment appointment2 = new Appointment();
        appointment2.setId(2L);
        appointment2.setDoctor(doctor2);
        appointment2.setDate(date1);
        appointment2.setStartTime(LocalTime.of(11, 0));
        appointment2.setStatus(AppointmentStatus.ACTIVE);

        Mockito.when(appointmentRepository.findAllByDoctorAndDateAndStatus(doctor1, date1, AppointmentStatus.ACTIVE))
                .thenReturn(Collections.singletonList(appointment1));

        Mockito.when(appointmentRepository.findAllByDoctorAndDateAndStatus(doctor2, date1, AppointmentStatus.ACTIVE))
                .thenReturn(Collections.singletonList(appointment2));

        AppointmentService appointmentService = new AppointmentService(emailService,
                doctorRepository, patientRepository, appointmentRepository, doctorScheduleRepository);

        Map<Long, Map<LocalDate, DayAvailability>> availabilityMap = appointmentService.getAvailabilityMap(doctors, dates);

        Map<LocalDate, DayAvailability> doctor1AvailabilityMap = availabilityMap.get(doctor1.getId());
        assertNotNull(doctor1AvailabilityMap);
        assertEquals(DayAvailability.NOT_IN_SCHEDULE, doctor1AvailabilityMap.get(date1));
        assertEquals(DayAvailability.NOT_IN_SCHEDULE, doctor1AvailabilityMap.get(date2));
    }
}