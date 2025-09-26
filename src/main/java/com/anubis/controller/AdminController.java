package com.anubis.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anubis.model.User;
import com.anubis.repository.ApplicationRepository;
import com.anubis.repository.PetRepository;
import com.anubis.repository.UserRepository;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:3000"})
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationRepository applicationRepository;
    
    @Autowired
    private PetRepository petRepository;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        try {
            Optional<User> user = userRepository.findById(userId);
            if (!user.isPresent()) {
                return ResponseEntity.status(404).body("Usuario no encontrado");
            }
            
            applicationRepository.deleteByUserId(userId);
            
            if ("FOUNDATION".equals(user.get().getRole().name())) {
                petRepository.deleteByFoundationId(userId);
            }
            
            userRepository.deleteById(userId);
            
            return ResponseEntity.ok().body("Usuario eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al eliminar usuario: " + e.getMessage());
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics() {
        try {
            long userCount = userRepository.count();
            long applicationCount = applicationRepository.count();
            long petCount = petRepository.count();
            
            return ResponseEntity.ok().body("""
                Estadísticas del sistema:
                Usuarios: %d
                Postulaciones: %d
                Mascotas: %d
                """.formatted(userCount, applicationCount, petCount));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al obtener estadísticas: " + e.getMessage());
        }
    }

    @DeleteMapping("/clear-all-data")
    public ResponseEntity<?> clearAllData() {
        try {
            userRepository.deleteAll();
            applicationRepository.deleteAll();
            
            return ResponseEntity.ok().body("Todos los datos han sido eliminados exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al eliminar datos: " + e.getMessage());
        }
    }

    @GetMapping("/count-data")
    public ResponseEntity<?> countData() {
        try {
            long userCount = userRepository.count();
            long applicationCount = applicationRepository.count();
            
            return ResponseEntity.ok().body("""
                Datos en base:
                Usuarios: %d
                Postulaciones: %d
                """.formatted(userCount, applicationCount));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al contar datos: " + e.getMessage());
        }
    }
}
