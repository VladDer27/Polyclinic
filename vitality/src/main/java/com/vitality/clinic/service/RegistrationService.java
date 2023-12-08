package com.vitality.clinic.service;

import com.vitality.clinic.model.User;
import com.vitality.clinic.repository.UserRepository;
import com.vitality.clinic.utils.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class RegistrationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public long register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(UserRole.ROLE_GUEST);
        return userRepository.save(user).getId();
    }

    public String encodePassword(String password) {
        return passwordEncoder.encode((password));
    }
}
