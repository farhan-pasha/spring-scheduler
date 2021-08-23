package com.keembay.scheduler.service;

import com.bugsnag.Bugsnag;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class EmailRenewalUtil {

    @Value("${spring.sendgrid.api-key}")
    private String SENDGRID_KEY;

    @Value("${spring.mail.username}")
    private String fromUser;

    @Autowired
    private Bugsnag bugsnag;

    public Response sendEmailSendgrid (String recipient, String subject, String msg) throws Exception{
        Email from = new Email(fromUser);
        Email to = new Email(recipient);
        Content content = new Content("text/plain", msg);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(SENDGRID_KEY);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            return response;
        } catch (IOException ex) {
            log.error(ex.getMessage());
            bugsnag.notify(ex);
            throw new Exception("Error while sending the email: "+ ex.getMessage());
        }
    }

}
