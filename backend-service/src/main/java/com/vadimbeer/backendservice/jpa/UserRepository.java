package com.vadimbeer.backendservice.jpa;

import com.vadimbeer.backendservice.DTO.UserResponse;
import com.vadimbeer.backendservice.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User getUserByUsername(String username);
    User getUserByEmail(String email);
}
