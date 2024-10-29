package com.backend.integrador;

import static org.mockito.Mockito.*;

import com.backend.integrador.Service.CorreosService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private CorreosService emailService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testEnviarCorreo() throws MessagingException {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        when(templateEngine.process(eq("emailTemplate"), any(Context.class)))
                .thenReturn("<html>Correo de prueba</html>");

        emailService.sendEmail("test@example.com", "Asunto", "emailTemplate", "Usuario");

        verify(mailSender, times(1)).send(mimeMessage);
    }
}
