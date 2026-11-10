package com.example.bookmyshow.Repository;

import com.example.bookmyshow.Entity.Users;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByEmail(String email);
    Optional<Users> findByEmailIgnoreCase(String email);

    Boolean existsByEmail(String email);
}
