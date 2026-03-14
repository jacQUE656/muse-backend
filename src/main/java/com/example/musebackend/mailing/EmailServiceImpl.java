package com.example.musebackend.mailing;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;


@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private SendGrid sendGrid; // This bean is created by the SDK

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Override
    public void sendMail(AbstractEmailContext email) {
        try {
            Context context = new Context();
            context.setVariables(email.getContext());
            // If this line fails, your app crashes before sending!
            String emailContent = templateEngine.process(email.getTemplateLocation(), context);

            Email from = new Email(email.getFrom());
            Email to = new Email(email.getTo());
            Content content = new Content("text/html", emailContent);
            Mail mail = new Mail(from, email.getSubject(), to, content);

            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            // This is where it hits the internet
            Response response = sendGrid.api(request);
            System.out.println("SendGrid Response Code: " + response.getStatusCode());

        } catch (Exception e) {
            // THIS IS THE MOST IMPORTANT PART
            System.err.println("CRITICAL ERROR IN EMAIL SERVICE:");
            e.printStackTrace();
        }
    }
}