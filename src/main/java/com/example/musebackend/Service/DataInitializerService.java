package com.example.musebackend.Service;

import com.example.musebackend.Models.User;
import com.example.musebackend.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataInitializerService implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        createDefaultAdminUser();
    }

    private void createDefaultAdminUser() {
        //check if admin exists
        if (!userRepository.existsByEmailIgnoreCase("admin65@gmail.com")){
           User adminUser =User.builder()
                    .firstname("Admin")
                    .lastname("Admin")
                    .email("admin65@gmail.com")
                    .phonenumber("+2349161367120")
                    .password(passwordEncoder.encode("admin:56"))
                    .role(User.Role.ADMIN)
                   .emailVerified(true)
                    .build();
            userRepository.save(adminUser);
            log.info("Default admin created : email = admin65@gmail.com , password = admin:56 ");
        }else {
            log.info("Admin already exists");
        }
    }
}
