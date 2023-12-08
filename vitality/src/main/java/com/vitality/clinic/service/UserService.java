package com.vitality.clinic.service;


import com.vitality.clinic.model.User;
import com.vitality.clinic.repository.UserRepository;
import com.vitality.clinic.utils.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RegistrationService registrationService;
    @Autowired
    public UserService(UserRepository userRepository, RegistrationService registrationService) {
        this.userRepository = userRepository;
        this.registrationService = registrationService;
    }

    public List<User> getAllUsers() {
        return  userRepository.findAll();
    }

    public List<User> getUsersByRole(UserRole role) {
        return userRepository.findAllByRole(role);
    }

    public User getUserById(long id) {
        return userRepository.getById(id);
    }

    public User getUserByLogin(String login) {
        return userRepository.findByLogin(login).orElse(null);
    }

    @Transactional
    public void updateUser(User user) {
        userRepository.save(user);
    }

    @Transactional
    public void deleteUserById(long userId) {
        userRepository.deleteById(userId);
    }

    public User updateExistingUserObject(User existingUser, User newUser) {
        existingUser.setFirstName(newUser.getFirstName());
        existingUser.setLastName(newUser.getLastName());
        existingUser.setMiddleName(newUser.getMiddleName());
        existingUser.setLogin(newUser.getLogin());
        existingUser.setPassword(registrationService.encodePassword(newUser.getPassword()));
        return existingUser;
    }
}
