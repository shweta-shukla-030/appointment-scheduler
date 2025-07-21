package com.example.appointmentscheduler.repository;

import com.example.appointmentscheduler.model.User;
import com.example.appointmentscheduler.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRole(UserRole role);
    List<User> findByLocation(String location);
    Optional<User> findByEmailAndPassword(String email, String password);
}
