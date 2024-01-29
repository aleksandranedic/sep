package com.example.authservice.service;
import com.example.authservice.model.Lawyer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MailingService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendEmailVerificationMail(Lawyer propertyOwner) {
        String content = renderTemplate(
                "firstName", propertyOwner.getFirstName(),
                "email", propertyOwner.getEmail(),
                "code", propertyOwner.getVerificationCode());

        sendMail(propertyOwner.getEmail(), "Welcome to Secure IT! Complete verification", content);
    }

    private void sendMail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        String senderAddress = "bsep-test@outlook.com";
        message.setFrom(senderAddress);

        mailSender.send(message);
    }

    private String renderTemplate(String... variables) {
        Map<String, String> variableMap = new HashMap<>();

        List<String> keyValueList = Arrays.asList(variables);

        if (keyValueList.size() % 2 != 0)
            throw new IllegalArgumentException();

        for (int i = 0; i < keyValueList.size(); i += 2) {
            variableMap.put(keyValueList.get(i), keyValueList.get(i + 1));
        }

        return renderTemplate(variableMap);
    }

    private String renderTemplate(Map<String, String> variables) {
        String message = "Dear {{ firstName }},\n\n" +
                "To start using our services, we need to verify your ownership of the email address {{ email }}. " +
                "Please click on the following link to verify your email address:\n\n" +
                "Click to verify: http://localhost:4200/registration/verification?code={{ code }}\n\n" +
                "Thank you for your cooperation.\n\n" +
                "Best regards,\nSEP";

        String target, renderedValue;
        for (var entry : variables.entrySet()) {
            target = "\\{\\{ " + entry.getKey() + " \\}\\}";
            renderedValue = entry.getValue();

            message = message.replaceAll(target, renderedValue);
        }

        return message;
    }
}
