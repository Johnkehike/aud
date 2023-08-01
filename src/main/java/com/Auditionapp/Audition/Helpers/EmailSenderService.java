package com.Auditionapp.Audition.Helpers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailSenderService {

    @Autowired
    private JavaMailSender mailSender;




        public void sendEmail(String toEmail, String subject, String body) throws MessagingException {

            MimeMessage message = mailSender.createMimeMessage();

            // Pass true to indicate you need a multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("info@mygiancarlo.com");
            helper.setTo(toEmail);
            helper.setText(body, true);
            helper.setSubject(subject);
            // Add the QR code image as an attachment

            mailSender.send(message);

            System.out.println("Mail Sent successfully");



        }
}
