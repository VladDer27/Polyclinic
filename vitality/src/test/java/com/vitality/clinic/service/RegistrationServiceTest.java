package com.vitality.clinic.service;

import com.vitality.clinic.model.User;
import com.vitality.clinic.repository.UserRepository;
import com.vitality.clinic.utils.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RegistrationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegistrationService registrationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegister() {
        User user = new User();
        user.setFirstName("firstname2");
        user.setLastName("lastname2");
        user.setLogin("second@mail.ru");
        user.setPassword("password");

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(user);

        long userId = registrationService.register(user);

        Mockito.verify(userRepository, Mockito.times(1)).save(user);
        assertEquals(user.getId(), userId);
        assertEquals(UserRole.ROLE_GUEST, user.getRole());
        assertEquals("encodedPassword", user.getPassword());
    }

    @Test
    public void testEncodePassword() {
        String password = "password";
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        String encodedPassword = registrationService.encodePassword(password);

        assertEquals("encodedPassword", encodedPassword);
    }
}
