package com.vitality.clinic.controller;

import com.vitality.clinic.model.Patient;
import com.vitality.clinic.model.User;
import com.vitality.clinic.service.UserService;
import com.vitality.clinic.utils.enums.Gender;
import com.vitality.clinic.utils.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/guest")
public class GuestController {
    private final UserService userService;
    @Autowired
    public GuestController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('GUEST') and #guestId == principal.id")
    @GetMapping("/edit/{guestId}")
    public String guestToPatientPage(@PathVariable long  guestId, Model model) {
        User user = userService.getUserById(guestId);
        model.addAttribute("user", user);
        model.addAttribute("patient", new Patient());
        model.addAttribute("genders", Gender.values());
        return "/guest/add-patient-data-to-guest";
    }

    @PreAuthorize("hasRole('GUEST') and #guestId == principal.id")
    @PostMapping("/edit/{guestId}")
    public String guestToPatient(@ModelAttribute User user,
                                 @ModelAttribute Patient patient,
                                 @PathVariable long guestId) {
        User existingUser = userService.getUserById(guestId);
        existingUser.setFirstName(user.getFirstName());
        existingUser.setMiddleName(user.getMiddleName());
        existingUser.setLastName(user.getLastName());
        existingUser.setPatient(patient);
        existingUser.setRole(UserRole.ROLE_PATIENT);
        patient.setUser(existingUser);
        userService.updateUser(existingUser);
        return "redirect:/auth/login?submit";
    }
}
