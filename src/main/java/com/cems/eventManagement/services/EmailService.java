package com.cems.eventManagement.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpMail(String toEmail, String otp){
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail); //kis ko bhejna hai
        message.setSubject("Welcome to College Events! Verify Your Email");
        message.setText("Hello,\n\nYour OTP for registration is: " + otp + "\n\nPlease do not share this OTP with anyone.\n\nThanks!");

        mailSender.send(message);
    }
}
