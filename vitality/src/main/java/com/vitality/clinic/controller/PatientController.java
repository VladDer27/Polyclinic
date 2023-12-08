package com.vitality.clinic.controller;

import com.vitality.clinic.model.Appointment;
import com.vitality.clinic.model.Patient;
import com.vitality.clinic.model.User;
import com.vitality.clinic.service.AppointmentService;
import com.vitality.clinic.service.PatientService;
import com.vitality.clinic.service.UserService;
import com.vitality.clinic.utils.enums.Gender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/patient")
public class PatientController {
    private final UserService userService;
    private final PatientService patientService;
    private final AppointmentService appointmentService;

    @Autowired
    public PatientController(UserService userService,
                             PatientService patientService,
                             AppointmentService appointmentService) {
        this.userService = userService;
        this.patientService = patientService;
        this.appointmentService = appointmentService;
    }

    @PreAuthorize("hasRole('PATIENT') and #patientId == principal.patient.id")
    @GetMapping("/edit/{patientId}")
    public String editPatientPage(@PathVariable long patientId, Model model) {
        Patient patient = patientService.getPatientById(patientId).get();
        model.addAttribute("genders", Gender.values());
        model.addAttribute("patient", patient);
        model.addAttribute("user", patient.getUser());
        return "/patient/edit-patient";
    }

    @PreAuthorize("hasRole('PATIENT') and #patientId == principal.patient.id")
    @PostMapping("/edit/{patientId}")
    public String editPatient(@ModelAttribute Patient patient,
                              @ModelAttribute User user,
                              @PathVariable long patientId) {
        Patient existingPatient = patientService.getPatientById(patientId).get();
        existingPatient.setDateOfBirth(patient.getDateOfBirth());
        existingPatient.setGender(patient.getGender());
        User existingUser = existingPatient.getUser();
        existingUser = userService.updateExistingUserObject(existingUser, user);
        userService.updateUser(existingUser);
        return "redirect:/home";
    }

    @PreAuthorize("hasRole('PATIENT') and #patientId == principal.patient.id or hasRole('ADMIN')")
    @GetMapping("/appointments/{patientId}")
    public String appointmentsPage(@PathVariable long patientId, Model model) {
        Patient patient = patientService.getPatientById(patientId).get();
        List<Appointment> appointments = appointmentService.getAppointmentsByPatient(patient);

        appointmentService.sortAppointmentsList(appointments);
        model.addAttribute("appointments",  appointments);
        return "/patient/appointments";
    }
}
