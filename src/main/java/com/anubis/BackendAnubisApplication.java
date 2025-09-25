package com.anubis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class BackendAnubisApplication {

    public static void main(String[] args) {
        // Cargar variables del archivo .env antes de iniciar Spring Boot
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing() // No fallar si .env no existe
                    .load();
            
            // Establecer variables como propiedades del sistema
            dotenv.entries().forEach(entry -> {
                System.setProperty(entry.getKey(), entry.getValue());
            });
            
            System.out.println("✅ Archivo .env cargado correctamente");
        } catch (Exception e) {
            System.out.println("⚠️ No se pudo cargar .env: " + e.getMessage());
        }
        
        SpringApplication.run(BackendAnubisApplication.class, args);
    }
}
