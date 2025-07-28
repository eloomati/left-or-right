package io.mhetko.lor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendConfirmationEmail(String to, String token) {
        String subject = "Potwierdzenie rejestracji";
        String confirmationUrl = "http://localhost:8080/api/users/confirm?token=" + token;
        String text = "Kliknij w link, aby potwierdzić rejestrację: " + confirmationUrl;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }
}