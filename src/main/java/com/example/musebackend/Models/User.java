package com.example.musebackend.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JsonProperty("id")
    private String id;

    @Column(name = "FIRST_NAME" ,  nullable = false)
    private  String firstname;
    @Column(name = "LAST_NAME" ,  nullable = false)
    private  String lastname;
    @Column(name = "EMAIL", unique = true, nullable = false)
    private  String email;
    @Column(name = "PHONE_NUMBER", unique = true, nullable = false)
    private  String phonenumber;

    @Column(name = "PASSWORD" ,  nullable = false)
    private  String password;
    @Column(name = "EMAIL_VERIFIED")
    private boolean emailVerified;


    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL , orphanRemoval = true)
    @Builder.Default
    private List<Playlist> playlists = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return emailVerified;
    }

    @Override
    public String getPassword() {
        return password;
    }


    public enum Role{
        USER , ADMIN
    }
}
