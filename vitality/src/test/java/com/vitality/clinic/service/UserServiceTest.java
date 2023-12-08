package com.vitality.clinic.service;

import com.vitality.clinic.model.User;
import com.vitality.clinic.repository.UserRepository;
import com.vitality.clinic.utils.enums.UserRole;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RegistrationService registrationService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllUsers() {
        List<User> userList = new ArrayList<>();
        User user1 = new User();
        user1.setId(1L);
        user1.setFirstName("firstname1");
        user1.setLastName("lastname1");
        user1.setLogin("first@mail.ru");
        user1.setPassword("password");
        user1.setRole(UserRole.ROLE_PATIENT);
        userList.add(user1);

        User user2 = new User();
        user2.setId(2L);
        user2.setFirstName("firstname2");
        user2.setLastName("lastname2");
        user2.setLogin("second@mail.ru");
        user2.setPassword("password");
        user2.setRole(UserRole.ROLE_PATIENT);
        userList.add(user2);

        when(userRepository.findAll()).thenReturn(userList);

        List<User> result = userService.getAllUsers();
        assertEquals(2, result.size());
        assertEquals(user1, result.get(0));
        assertEquals(user2, result.get(1));
    }

    @Test
    public void testGetUsersByRole() {
        User user1 = new User();
        user1.setId(1L);
        user1.setFirstName("firstname1");
        user1.setLastName("lastname1");
        user1.setLogin("first@mail.ru");
        user1.setPassword("password");
        user1.setRole(UserRole.ROLE_PATIENT);

        User user2 = new User();
        user2.setId(2L);
        user2.setFirstName("firstname2");
        user2.setLastName("lastname2");
        user2.setLogin("second@mail.ru");
        user2.setPassword("password");
        user2.setRole(UserRole.ROLE_ADMIN);

        User user3 = new User();
        user3.setId(3L);
        user3.setFirstName("firstname3");
        user3.setLastName("lastname3");
        user3.setLogin("third@mail.ru");
        user3.setPassword("password");
        user3.setRole(UserRole.ROLE_PATIENT);

        when(userRepository.findAllByRole(UserRole.ROLE_PATIENT)).thenReturn(Arrays.asList(user1, user3));
        when(userRepository.findAllByRole(UserRole.ROLE_ADMIN)).thenReturn(Collections.singletonList(user2));

        List<User> result1 = userService.getUsersByRole(UserRole.ROLE_PATIENT);
        assertEquals(2, result1.size());
        assertEquals(user1, result1.get(0));
        assertEquals(user3, result1.get(1));

        List<User> result2 = userService.getUsersByRole(UserRole.ROLE_ADMIN);
        assertEquals(1, result2.size());
        assertEquals(user2, result2.get(0));
    }

    @Test
    public void testGetUserById() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("firstname");
        user.setLastName("lastname");
        user.setLogin("user@mail.ru");
        user.setPassword("password");
        user.setRole(UserRole.ROLE_PATIENT);

        when(userRepository.getById(1L)).thenReturn(user);

        User result = userService.getUserById(1L);
        assertEquals(user, result);
    }

    @Test
    public void testGetUserByLogin() {
        String login = "user@mail.ru";
        User expectedUser = new User();
        expectedUser.setLogin(login);
        Mockito.when(userRepository.findByLogin(login)).thenReturn(Optional.of(expectedUser));

        User resultUser = userService.getUserByLogin(login);

        Assert.assertEquals(expectedUser, resultUser);
    }

    @Test
    public void testUpdateUser() {
        User user = new User();
        user.setId(1L);
        Mockito.when(userRepository.save(user)).thenReturn(user);

        userService.updateUser(user);

        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    @Test
    public void testDeleteUserById() {
        long userId = 1L;

        userService.deleteUserById(userId);

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(userId);
    }

    @Test
    public void testUpdateExistingUserObject() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setLogin("olduser@mail.ru");
        existingUser.setPassword("oldpassword");
        User newUser = new User();
        newUser.setLogin("newuser@mail.ru");
        newUser.setPassword("newpassword");

        Mockito.when(registrationService.encodePassword(newUser.getPassword())).thenReturn("encodedPassword");

        User updatedUser = userService.updateExistingUserObject(existingUser, newUser);

        Assert.assertEquals(newUser.getFirstName(), updatedUser.getFirstName());
        Assert.assertEquals(newUser.getLastName(), updatedUser.getLastName());
        Assert.assertEquals(newUser.getMiddleName(), updatedUser.getMiddleName());
        Assert.assertEquals(newUser.getLogin(), updatedUser.getLogin());
        Assert.assertEquals("encodedPassword", updatedUser.getPassword());
    }
}
