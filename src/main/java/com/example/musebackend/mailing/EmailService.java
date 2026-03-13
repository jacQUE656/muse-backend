package com.example.musebackend.mailing;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendMail (final AbstractEmailContext email) throws MessagingException;
}
