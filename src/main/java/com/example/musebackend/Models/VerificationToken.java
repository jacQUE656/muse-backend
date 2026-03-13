package com.example.musebackend.Models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "verification_token")
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false , name = "user_id")
    private User user;

    private LocalDateTime expireDate;


}
