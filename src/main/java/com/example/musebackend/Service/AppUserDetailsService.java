package com.example.musebackend.Service;

import com.example.musebackend.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    @Autowired
    private  UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        com.example.musebackend.Models.User Euser =  this.userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found with email" + email));
        return Euser;
                  }
}
