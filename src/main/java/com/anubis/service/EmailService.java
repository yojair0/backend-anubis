package com.anubis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.name:Anubis Adoption Platform}")
    private String appName;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendVerificationCodeEmail(String email, String fullName, String verificationCode) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("Código de verificación - " + appName);
            
            String text = String.format(
                "Hola %s,\n\n" +
                "¡Bienvenido a %s!\n\n" +
                "Tu código de verificación es:\n\n" +
                "🔑 %s\n\n" +
                "Este código expira en 15 minutos.\n\n" +
                "Si no creaste esta cuenta, puedes ignorar este email.\n\n" +
                "¡Gracias por unirte a nuestra comunidad!\n\n" +
                "El equipo de %s",
                fullName, appName, verificationCode, appName
            );
            
            message.setText(text);
            mailSender.send(message);
            
            System.out.println("CODIGO DE VERIFICACION ENVIADO:");
            System.out.println("Para: " + email);
            System.out.println("Código: " + verificationCode);
            
        } catch (Exception e) {
            System.err.println("ERROR enviando código de verificación: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error enviando email de verificación", e);
        }
    }

    public void sendVerificationEmail(String email, String fullName, String verificationToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("Verifica tu cuenta - " + appName);
            
            String verificationUrl = frontendUrl + "/verify-email?token=" + verificationToken;
            String text = String.format(
                "Hola %s,\n\n" +
                "¡Bienvenido a %s!\n\n" +
                "Para completar tu registro, por favor verifica tu dirección de email haciendo clic en el siguiente enlace:\n\n" +
                "%s\n\n" +
                "Si no creaste esta cuenta, puedes ignorar este email.\n\n" +
                "¡Gracias por unirte a nuestra comunidad!\n\n" +
                "El equipo de %s",
                fullName, appName, verificationUrl, appName
            );
            
            message.setText(text);
            mailSender.send(message);
            
            System.out.println("✅ EMAIL DE VERIFICACIÓN ENVIADO:");
            System.out.println("Para: " + email);
            System.out.println("URL: " + verificationUrl);
            System.out.println("Token: " + verificationToken);
            
        } catch (Exception e) {
            System.err.println("❌ ERROR ENVIANDO EMAIL: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendApplicationStatusEmail(String email, String fullName, String petName, String status, String foundationResponse) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            
            String subject = "ACCEPTED".equals(status) ? 
                "¡Tu postulación fue ACEPTADA! - " + appName :
                "Actualización de tu postulación - " + appName;
                
            String statusText = "ACCEPTED".equals(status) ?
                "¡Felicitaciones! Tu postulación para adoptar a " + petName + " ha sido ACEPTADA." :
                "Tu postulación para adoptar a " + petName + " ha sido " + status.toLowerCase() + ".";
            
            String text = String.format(
                "Hola %s,\n\n" +
                "%s\n\n" +
                "%s\n\n" +
                "Puedes revisar el estado de todas tus postulaciones en tu panel de usuario.\n\n" +
                "¡Gracias por ser parte de nuestra comunidad!\n\n" +
                "El equipo de %s",
                fullName, statusText, 
                (foundationResponse != null && !foundationResponse.trim().isEmpty()) ? 
                    "Mensaje de la fundación: " + foundationResponse : "",
                appName
            );
            
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            
            System.out.println("✅ NOTIFICACIÓN ENVIADA:");
            System.out.println("Para: " + email);
            System.out.println("Estado: " + status);
            
        } catch (Exception e) {
            System.err.println("❌ ERROR ENVIANDO NOTIFICACIÓN: " + e.getMessage());
        }
    }

    public void sendPasswordResetEmail(String email, String fullName, String resetToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("Recuperar contraseña - " + appName);
            
            String resetUrl = frontendUrl + "/reset-password?token=" + resetToken;
            String text = String.format(
                "Hola %s,\n\n" +
                "Recibimos una solicitud para restablecer la contraseña de tu cuenta en %s.\n\n" +
                "Si fuiste tú quien hizo esta solicitud, haz clic en el siguiente enlace para crear una nueva contraseña:\n\n" +
                "%s\n\n" +
                "Este enlace expirará en 1 hora por seguridad.\n\n" +
                "Si no solicitaste un restablecimiento de contraseña, puedes ignorar este email.\n\n" +
                "El equipo de %s",
                fullName, appName, resetUrl, appName
            );
            
            message.setText(text);
            mailSender.send(message);
            
            System.out.println("✅ EMAIL DE RESET ENVIADO:");
            System.out.println("Para: " + email);
            System.out.println("URL: " + resetUrl);
            
        } catch (Exception e) {
            System.err.println("❌ ERROR ENVIANDO RESET: " + e.getMessage());
        }
    }
}
