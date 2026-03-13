package com.example.musebackend.mailing;

import com.example.musebackend.Models.User;
import org.springframework.web.util.UriComponentsBuilder;

public class PasswordResetEmailContext extends AbstractEmailContext{

    private String token;


    @Override
    public <T> void init(T context) {
        User user = (User) context;

        put("firstName", user.getFirstname());
        setTemplateLocation("mailing/reset-password");
        setSubject("Reset Password");
        setFrom("jamestonibor65@gmail.com");
        setTo(user.getEmail());
    }

    public void setToken(String token) {
        this.token = token;
        put("token", token);
    }

    public void buildVerificationUrl(final String baseURL, final String token) {
        String url = UriComponentsBuilder.fromUriString(baseURL)
                .path("/reset-password")
                .queryParam("token", token)
                .build()
                .toUriString();
        put("passwordResetUrl", url);
    }

}
