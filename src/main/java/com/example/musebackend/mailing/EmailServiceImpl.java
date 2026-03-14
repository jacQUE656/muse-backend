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

import java.io.IOException;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private SendGrid sendGrid; // This bean is created by the SDK

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Override
    public void sendMail(AbstractEmailContext email) throws IOException {
        // 1. Process the Thymeleaf template
        Context context = new Context();
        context.setVariables(email.getContext());
        String emailContent = templateEngine.process(email.getTemplateLocation(), context);

        // 2. Build the SendGrid Mail object
        Email from = new Email(email.getFrom());
        Email to = new Email(email.getTo());
        Content content = new Content("text/html", emailContent);
        Mail mail = new Mail(from, email.getSubject(), to, content);

        // 3. Send via API
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sendGrid.api(request);

        // Log status (202 Accepted indicates success)
        System.out.println("Status Code: " + response.getStatusCode());
    }
}