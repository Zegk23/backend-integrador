package com.backend.integrador.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class CorreosService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendEmail(String to, String subject, String templateName, String nombre) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setFrom("velazcopanaderiaydulceria@gmail.com");

        // Configuración de Thymeleaf
        Context context = new Context();
        context.setVariable("nombre", nombre); // Variable que se pasa a la plantilla

        // Renderización de la plantilla
        String htmlContent = templateEngine.process(templateName, context);
        helper.setText(htmlContent, true); // Habilita HTML

        mailSender.send(message);
    }
}
