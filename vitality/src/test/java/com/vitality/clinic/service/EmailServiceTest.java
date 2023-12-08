package com.vitality.clinic.service;

import com.vitality.clinic.model.Appointment;
import com.vitality.clinic.model.Doctor;
import com.vitality.clinic.model.Patient;
import com.vitality.clinic.model.User;
import com.vitality.clinic.utils.enums.AppointmentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSenderImpl emailSender;

    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        emailService = new EmailService(emailSender, templateEngine);
    }

    @Test
    public void testAppointmentMessage() throws Exception {
        Appointment appointment = new Appointment();
        appointment.setId(1L);

        User patientUser = new User();
        patientUser.setLogin("patient@mail.ru");

        User doctorUser = new User();
        doctorUser.setLogin("doctor@mail.ru");

        Patient patient = new Patient();
        patient.setUser(patientUser);
        appointment.setPatient(patient);

        Doctor doctor = new Doctor();
        doctor.setUser(doctorUser);
        appointment.setDoctor(doctor);

        AppointmentStatus status = AppointmentStatus.ACTIVE;

        MimeMessage message = new MimeMessage((Session) null);
        when(emailSender.createMimeMessage()).thenReturn(message);

        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//        whenNew(MimeMessageHelper.class).withArguments(eq(message), eq(true), eq("UTF-8")).thenReturn(helper);

        String htmlContent = "<html><body>Test email content</body></html>";
//        when(templateEngine.process(anyString(), any(Context.class))).thenReturn(htmlContent);

//        emailService.appointmentMessage(appointment, status);

//        verify(emailSender).send(message);
    }

}