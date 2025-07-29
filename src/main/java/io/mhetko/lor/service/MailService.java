package io.mhetko.lor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendConfirmationEmail(@NonNull String to, @NonNull String token) {
        String subject = "Registration confirmation";
        String confirmationUrl = "http://localhost:8080/api/users/confirm?token=" + token;
        String text = "Click the link to confirm your registration: " + confirmationUrl;
        sendEmail(to, subject, text);
    }

    private void sendEmail(@NonNull String to, @NonNull String subject, @NonNull String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            log.info("Sending email to: {}, subject: {}", to, subject);
            mailSender.send(message);
            log.info("Email sent to: {}", to);
        } catch (Exception e) {
            log.error("Error while sending email to: {}", to, e);
            throw e;
        }
    }
}