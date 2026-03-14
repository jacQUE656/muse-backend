package com.example.musebackend.mailing;


import java.io.IOException;

public interface EmailService {
    void sendMail (final AbstractEmailContext email) throws  IOException;
}
