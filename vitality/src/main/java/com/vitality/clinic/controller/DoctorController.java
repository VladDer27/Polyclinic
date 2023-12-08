package com.vitality.clinic.controller;

import com.vitality.clinic.model.Appointment;
import com.vitality.clinic.model.Doctor;
import com.vitality.clinic.service.AppointmentService;
import com.vitality.clinic.service.DoctorService;
import com.vitality.clinic.utils.enums.AppointmentStatus;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;


@Controller
@RequestMapping("/doctor")
public class DoctorController {
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;

    public DoctorController(DoctorService doctorService, AppointmentService appointmentService) {
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
    }

    @PreAuthorize("hasRole('DOCTOR') and #doctorId == principal.doctor.id or hasRole('ADMIN')")
    @GetMapping("/appointments/{doctorId}")
    public String appointmentsPage(@PathVariable long doctorId,
                                   @RequestParam(required = false)
                                   @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, Model model) {
        Doctor doctor = doctorService.getDoctorById(doctorId).get();
        List<Appointment> appointments = date == null ?
                appointmentService.getAppointmentsByDoctorAndStatus(doctor, AppointmentStatus.ACTIVE) :
                appointmentService.getAppointmentsByDoctorAndDateAndStatus(doctor, date, AppointmentStatus.ACTIVE);

        appointmentService.sortAppointmentsList(appointments);
        model.addAttribute("dates", appointmentService.getSortedListOfDates(
                appointmentService.getAppointmentsByDoctorAndStatus(doctor, AppointmentStatus.ACTIVE)));
        model.addAttribute("appointments",  appointments);
        return "/doctor/appointments";
    }
}
