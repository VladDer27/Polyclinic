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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void loadUserByUsername_returnsUserDetails() {
        String username = "user";
        User user = new User();
        user.setLogin(username);
        user.setPassword("password");
        user.setRole(UserRole.ROLE_PATIENT);
        when(userRepository.findByLogin(username)).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority(user.getRole().name())));
    }

    @Test
    void loadUserByUsername_userNotFound_throwsUsernameNotFoundException() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        UserDetailsService userDetailsService = new UserDetailsService(userRepository);
        Mockito.when(userRepository.findByLogin(Mockito.anyString())).thenReturn(Optional.empty());

        String username = "nonExistingUser";
        Throwable exception = assertThrows(UsernameNotFoundException.class, () ->
                userDetailsService.loadUserByUsername(username));

        assertEquals("User not found", exception.getMessage());
    }
}
