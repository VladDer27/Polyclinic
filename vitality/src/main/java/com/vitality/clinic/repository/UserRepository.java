package com.vitality.clinic.repository;

import com.vitality.clinic.model.User;
import com.vitality.clinic.utils.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllByRole(UserRole role);
    Optional<User> findByLogin(String login);
}
