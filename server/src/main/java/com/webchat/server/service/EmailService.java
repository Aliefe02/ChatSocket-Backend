package com.webchat.server.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String emailFrom;

    public boolean sendEmail(String receiver, String subject, String body, boolean isHtml){

        try{

            if (isHtml) {   // Html body
                MimeMessage message = emailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);

                helper.setTo(receiver);
                helper.setSubject(subject);
                helper.setText(body, true);
                helper.setFrom(emailFrom);
                emailSender.send(message);
            }
            else{   // Text body
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(receiver);
                message.setSubject(subject);
                message.setText(body);
                message.setFrom(emailFrom);
                emailSender.send(message);
            }

        } catch (MailException | MessagingException e){
            e.printStackTrace();
            return false;
        }

        System.out.println("Email sent to " + receiver);
        return true;
    }

}
