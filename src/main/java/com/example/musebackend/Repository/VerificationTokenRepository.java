package com.example.musebackend.Repository;

import com.example.musebackend.Models.User;
import com.example.musebackend.Models.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken , String> {
    Optional<VerificationToken> findByToken(String code);

    void deleteByUser(User user);
}
