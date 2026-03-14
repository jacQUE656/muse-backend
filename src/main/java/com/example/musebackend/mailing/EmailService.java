package com.example.musebackend.mailing;

import jakarta.mail.MessagingException;

import java.io.IOException;

public interface EmailService {
    void sendMail (final AbstractEmailContext email) throws MessagingException, IOException;
}
