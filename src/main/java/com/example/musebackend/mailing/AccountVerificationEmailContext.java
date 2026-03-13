package com.example.musebackend.mailing;

import com.example.musebackend.Models.User;
import org.springframework.web.util.UriComponentsBuilder;


public class AccountVerificationEmailContext extends AbstractEmailContext {
    private String token;


    @Override
    public <T> void init(T context) {
        User user = (User) context;

        put("firstName", user.getFirstname());
        setTemplateLocation("mailing/email-verification");
        setSubject("Complete Your Registration");
        setFrom("jamestoibor65@gmail.com");
        setTo(user.getEmail());
    }

    public void setToken(String token) {
        this.token = token;
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
