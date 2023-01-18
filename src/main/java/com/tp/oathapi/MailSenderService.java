package com.tp.oathapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailSenderService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String toEmail, String subject, String body) {
        MailConfiguration mailConfiguration = new MailConfiguration();
        mailSender = mailConfiguration.getJavaMailSender();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("jock8musicchannel@gmail.com");
        message.setTo(toEmail);
        message.setText(body);
        message.setSubject(subject);
        System.out.println(mailSender);
        mailSender.send(message);

    }

}
