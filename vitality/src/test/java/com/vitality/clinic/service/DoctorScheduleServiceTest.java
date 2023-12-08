package com.vitality.clinic.service;

import com.vitality.clinic.model.Doctor;
import com.vitality.clinic.repository.AppointmentRepository;
import com.vitality.clinic.repository.DoctorScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class DoctorScheduleServiceTest {

    @Mock
    private EmailService emailService;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorScheduleRepository doctorScheduleRepository;

    @InjectMocks
    private DoctorScheduleService doctorScheduleService;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testMakeDoctorScheduleFromRequest() {
        Doctor doctor = new Doctor();
        HttpServletRequest request = mock(HttpServletRequest.class);
        Map<String, String[]> params = new HashMap<>();
        params.put("MONDAY_start", new String[]{"09:00:00"});
        params.put("MONDAY_end", new String[]{"18:00:00"});
        params.put("TUESDAY_start", new String[]{""});
        params.put("TUESDAY_end", new String[]{""});
        Mockito.when(request.getParameterMap()).thenReturn(params);

        doctorScheduleService.makeDoctorScheduleFromRequest(doctor, request);

        assertEquals(doctor.getSchedules().size(), 1);
    }
}
