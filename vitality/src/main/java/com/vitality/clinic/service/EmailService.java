package com.vitality.clinic.service;

import com.vitality.clinic.model.Appointment;
import com.vitality.clinic.utils.enums.AppointmentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {
    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;

    @Autowired
    public EmailService(JavaMailSenderImpl emailSender,
                        TemplateEngine templateEngine) {
        this.emailSender = emailSender;
        this.templateEngine = templateEngine;
    }

    @Async
    public void appointmentMessage(Appointment appointment, AppointmentStatus status) {
        MimeMessage message = emailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("vitality-clinic@yandex.ru");
            helper.setTo(appointment.getPatient().getUser().getLogin());
            helper.setSubject(status.name().equals("ACTIVE") ?
                    "Заявка на запись подтверждена" : "Отмена записи");

            Context context = new Context();
            context.setVariable("appointment", appointment);
            context.setVariable("doctor", appointment.getDoctor());
            context.setVariable("patient_user", appointment.getPatient().getUser());
            context.setVariable("doctor_user", appointment.getDoctor().getUser());
            String htmlContent = templateEngine.process(status.name().equals("ACTIVE") ?
                    "email/new-appointment" : "email/cancel-appointment", context);
            helper.setText(htmlContent, true);
            emailSender.send(message);
        } catch (MessagingException e) {
            System.out.println("Ошибка при отправке сообщения");
        }
    }
}
