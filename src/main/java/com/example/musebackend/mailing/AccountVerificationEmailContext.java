package com.example.musebackend.mailing;

import com.example.musebackend.Models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.UriComponentsBuilder;

public class AccountVerificationEmailContext extends AbstractEmailContext {

    @Value("${app.mail.from}")
    private String senderEmail;

    @Override
    public <T> void init(T context) {
        User user = (User) context;
        put("firstName", user.getFirstname());
        setTemplateLocation("mailing/email-verification");
        setSubject("Complete Your Registration");
        setFrom(senderEmail);
        setTo(user.getEmail());

    }

    public void setToken(String token) {
        put("token", token);
    }

    public void buildVerificationUrl(final String baseURL, final String token) {
        String url = UriComponentsBuilder.fromUriString(baseURL)
                .path("/verify")
                .queryParam("token", token)
                .build()
                .toUriString();
        put("verificationURL", url);
    }
}