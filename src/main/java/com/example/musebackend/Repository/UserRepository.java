package com.example.musebackend.Repository;

import com.example.musebackend.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User , String> {


    boolean existsByEmailIgnoreCase(String email);

    User findByEmail(String email);
    Optional<User> findByEmailIgnoreCase(String email);

  boolean existsByPhonenumber(String phoneNumber);

}
