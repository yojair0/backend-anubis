package com.anubis.controller;

import com.anubis.repository.UserRepository;
import com.anubis.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:3000"})
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @DeleteMapping("/clear-all-data")
    public ResponseEntity<?> clearAllData() {
        try {
            // Borrar todos los usuarios
            userRepository.deleteAll();
            
            // Borrar todas las aplicaciones  
            applicationRepository.deleteAll();
            
            return ResponseEntity.ok().body("✅ Todos los datos han sido eliminados exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error al eliminar datos: " + e.getMessage());
        }
    }

    @GetMapping("/count-data")
    public ResponseEntity<?> countData() {
        try {
            long userCount = userRepository.count();
            long applicationCount = applicationRepository.count();
            
            return ResponseEntity.ok().body(
                "📊 Datos en base:\n" +
                "👥 Usuarios: " + userCount + "\n" +
                "📋 Aplicaciones: " + applicationCount
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error al contar datos: " + e.getMessage());
        }
    }
}
